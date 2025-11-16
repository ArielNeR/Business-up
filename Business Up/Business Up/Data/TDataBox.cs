using Box.V2;
using Box.V2.CCGAuth;
using Box.V2.Config;
using Box.V2.Models;
using Business_Up.Controls;
using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;

namespace Business_Up.Data
{
    public class TDataBox
    {
        private static int nComentario = 0;

        /**
        Este método devuelve una sesión autenticada de Box para el usuario especificado.
        @return Un objeto IBoxClient que representa la sesión autenticada del usuario.
        @throws BoxException si no se puede autenticar al usuario o si ocurre algún otro error.
        */
        private static async Task<IBoxClient> SesionAsync()
        {
            var boxConfig = new BoxConfigBuilder("5rb6426anp1rg4ki7ay4rorm7nbdvldc", "qj0NQoylJ1MjZCo6AR6gdLpXKcI3xvUG").Build();
            var boxCCG = new BoxCCGAuth(boxConfig);
            var userToken = await boxCCG.UserTokenAsync("21064068694");
            IBoxClient userClient = boxCCG.UserClient(userToken, "21064068694");
            return userClient;
        }

        /**
        Este método busca en la raíz de la cuenta de Box asociada al cliente proporcionado si ya existe un archivo con el mismo nombre que el archivo ubicado en la ruta filePath.
        @param client Cliente de Box para realizar la consulta.
        @param filePath Ruta del archivo a comprobar.
        @return Retorna un string con el ID del archivo encontrado en caso de existir o -1 si no existe.
        */
        private static async Task<string> Exist(IBoxClient client, string filePath)
        {
            var idFile = "-1";
            var files = await client.FoldersManager.GetFolderItemsAsync("0", 500);
            foreach (var file in files.Entries)
            {
                if (file.Name.Equals(Path.GetFileName(filePath)))
                {
                    idFile = file.Id;
                    break;
                }
            }
            return idFile;
        }

        /// <summary>
        /// Obtiene la lista de las cuentas suspendidas en la papelera de Box.
        /// </summary>
        /// <returns>Una lista de objetos BoxItem que representan las cuentas suspendidas.</returns>
        public static async Task<List<BoxItem>> CuentasSuspendidas()
        {
            List<BoxItem> result = new List<BoxItem>();
            try
            {
                var client = await SesionAsync();
                var trashItems = await client.FoldersManager.GetTrashItemsAsync(limit: 100, offset: 0);
                foreach (var item in trashItems.Entries)
                {
                    Console.WriteLine(item.Name);
                    result.Add(item);
                }
                return result;
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error al listar las cuentas suspendidas:\n\n" + ex.Message);
                DependencyService.Get<IMensaje>().ShowMessage("Error al listar las cuentas suspendidas\n\n" + ex.Message, 0);
                return result;
            }
        }

        /// <summary>
        /// Desbloquea una cuenta suspendida mediante el nombre de cuenta especificado.
        /// </summary>
        /// <param name="NombreCuenta">El nombre de la cuenta a desbloquear.</param>
        /// <returns>Una tarea que se completa cuando se ha desbloqueado la cuenta.</returns>
        public static async Task DesbloquearCuentaSuspendida(string NombreCuenta)
        {
            try
            {
                var client = await SesionAsync();
                var trashItems = await client.FoldersManager.GetTrashItemsAsync(limit: 100, offset: 0);
                foreach (var item in trashItems.Entries)
                {
                    if(item.Name == $"{NombreCuenta}.save")
                    {
                        BoxFileRequest requestParams = new BoxFileRequest()
                        {
                            Id = item.Id
                        };
                        await client.FilesManager.RestoreTrashedAsync(requestParams);
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error al desbloquear cuenta suspendida\n\n" + ex.Message);
                DependencyService.Get<IMensaje>().ShowMessage("Error al desbloquear cuenta suspendida\n\n" + ex.Message, 0);
            }
        }

        /// <summary>
        /// Bloquea la cuenta especificada mediante la eliminación del archivo que representa la cuenta en la papelera de Box.
        /// </summary>
        /// <param name="NombreCuenta">El nombre de la cuenta a bloquear.</param>
        /// <returns>Una tarea que representa la operación de bloqueo de la cuenta.</returns>
        public static async Task BloquearCuenta(string NombreCuenta)
        {
            try
            {
                var client = await SesionAsync();
                var exist = await Exist(client, $"{NombreCuenta}.save");
                if (exist != "-1")
                {
                    await client.FilesManager.DeleteAsync(exist);
                    Console.WriteLine("Cuentaaa bloqueadaaa");
                }
                else
                    DependencyService.Get<IMensaje>().ShowMessage("Cuenta no encontrada", 0);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error al bloquear cuenta\n\n" + ex.Message);
                DependencyService.Get<IMensaje>().ShowMessage("Error al bloquear cuenta\n\n" + ex.Message, 0);
            }
        }

        /// <summary>
        /// Uploads a file stream to Box and returns the status of the upload.
        /// </summary>
        /// <param name="dat">The file stream to upload.</param>
        /// <param name="user">The user associated with the file.</param>
        /// <param name="modo">The mode to use for uploading, where 0 is for updating an existing file and 1 is for creating a new file.</param>
        /// <returns>A string indicating the status of the upload.</returns>
        public static async Task<string> UploadAsync(Stream dat, string user, int modo)
        {
            try
            {
                var filePath = $"{user}.save";
                var client = await SesionAsync();
                if (modo == 0)
                {
                    var exist = await Exist(client, filePath);
                    if (exist != "-1")
                    {
                        BoxFile file = await client.FilesManager.UploadNewVersionAsync(filePath, exist, dat);
                        Console.WriteLine("Actualizado");
                        return "Actualizado";
                    }
                    else
                    {
                        BoxFileRequest requestParams = new BoxFileRequest()
                        {
                            Name = filePath,
                            Parent = new BoxRequestEntity() { Id = "0" }
                        };

                        BoxFile file = await client.FilesManager.UploadAsync(requestParams, dat);
                        Console.WriteLine("Subido");
                        return "Subido";

                    }
                }
                else
                {
                    var exist = await Exist(client, filePath);
                    if (exist == "-1")
                    {
                        BoxFileRequest requestParams = new BoxFileRequest()
                        {
                            Name = filePath,
                            Parent = new BoxRequestEntity() { Id = "0" }
                        };

                        BoxFile file = await client.FilesManager.UploadAsync(requestParams, dat);
                        Console.WriteLine("Subido");
                        return "Subido";
                    }
                    else
                        return "Usuario ya existente";

                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return "Error: " + ex.Message;

            }
        }

        /// <summary>
        /// Descarga el archivo guardado del usuario especificado.
        /// </summary>
        /// <param name="user">El nombre del usuario cuyo archivo debe ser descargado.</param>
        /// <returns>Un objeto Stream que representa el contenido del archivo descargado.</returns>
        public static async Task<Stream> DownloadAsync(string user)
        {
            string fileName = $"{user}.save";
            var client = await SesionAsync();
            var idFile = "";
            var files = await client.FoldersManager.GetFolderItemsAsync("0", 500);
            foreach (var file in files.Entries)
            {
                if (file.Name.Equals(fileName))
                {
                    idFile = file.Id;
                    break;
                }
            }
            Stream fileContents = await client.FilesManager.DownloadAsync(id: idFile);
            Console.WriteLine("Archivo descargado");
            return fileContents;
        }

        /// <summary>
        /// Comprueba si el usuario y la contraseña proporcionados existen en el sistema.
        /// </summary>
        /// <param name="Usuario">El nombre de usuario a comprobar.</param>
        /// <param name="Contra">La contraseña del usuario a comprobar.</param>
        /// <returns>Un entero que indica si el usuario existe y la contraseña es correcta o no, o si se produjo un error.</returns>
        public static async Task<int> ExistUser(string Usuario, string Contra)
        {
            try
            {
                var client = await SesionAsync();
                var idFile = "-1";
                string fileName = $"{Usuario}.save";
                var files = await client.FoldersManager.GetFolderItemsAsync("0", 500);
                foreach (var file in files.Entries)
                {
                    if (file.Name.Equals(fileName))
                    {
                        idFile = file.Id;
                        break;
                    }
                }
                if (idFile != "-1")
                {
                    BinaryFormatter BF = new BinaryFormatter();
                    var data = await TDataBox.DownloadAsync(Usuario);
                    List<object> DatosCargados = (List<object>)BF.Deserialize(data);
                    Usuario usuarioDatos = (Usuario)DatosCargados.ElementAt(4);
                    if (usuarioDatos.Contra == Contra)
                    {
                        return 0;
                    }
                    else
                    {
                        return 1;
                    }
                }
                else
                {
                    return 2;
                }
            }
            catch (Exception)
            {
                return -1;
            }

        }

        /**
        Método que permite subir una publicación a la carpeta correspondiente del usuario actual.
        @param dat Stream de datos de la publicación a subir.
        @return string que indica el resultado de la operación ("Subido" o "Actualizado") o un mensaje de error en caso de fallo.
        */
        public static async Task<string> UploadAsyncPublicacion(Stream dat)
        {
            try
            {
                var filePath = $"Publicaciones - {UsuarioData.UsuarioActual.Nombre}.save";
                var client = await SesionAsync();
                var exist = await Exist(client, filePath);
                if (exist != "-1")
                {
                    BoxFile file = await client.FilesManager.UploadNewVersionAsync(filePath, exist, dat);
                    Console.WriteLine("Actualizado");
                    return "Actualizado";
                }
                else
                {
                    BoxFileRequest requestParams = new BoxFileRequest()
                    {
                        Name = filePath,
                        Parent = new BoxRequestEntity() { Id = "0" }
                    };

                    BoxFile file = await client.FilesManager.UploadAsync(requestParams, dat);
                    Console.WriteLine("Subido");
                    return "Subido";

                }
                
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return "Error: " + ex.Message;

            }
        }

        /**
        Método para subir un archivo de comentario a la carpeta principal de Box.
        @param dat Un stream que contiene los datos del archivo a subir.
        */
        public static async Task UploadAsyncComentario(Stream dat)
        {
            try
            {
                var filePath = $"Comentario {nComentario} - {UsuarioData.UsuarioActual.Nombre}.pdf";
                var client = await SesionAsync();
                var exist = await Exist(client, filePath);
                if (exist != "-1")
                {
                    nComentario++;
                    await UploadAsyncComentario(dat);
                }
                else
                {
                    BoxFileRequest requestParams = new BoxFileRequest()
                    {
                        Name = filePath,
                        Parent = new BoxRequestEntity() { Id = "0" }
                    };

                    BoxFile file = await client.FilesManager.UploadAsync(requestParams, dat);
                    Console.WriteLine("Subido");
                }

            }
            catch (Exception ex)
            {
                Console.WriteLine("Error al subir el comentario:\n\n" + ex.Message);
                DependencyService.Get<IMensaje>().ShowMessage("Error al subir el comentario\n\n" + ex.Message, 0);

            }
        }

        /// <summary>
        /// Descarga las publicaciones de la carpeta raíz del usuario actual.
        /// </summary>
        /// <returns>Una lista de flujos de datos que representan los archivos descargados.</returns>
        public static async Task<List<Stream>> DownloadAsyncPublicacion()
        {
            try
            {
                string fileName = $"Publicaciones -";
                var client = await SesionAsync();
                List<Stream> fileContents = new List<Stream>();
                var files = await client.FoldersManager.GetFolderItemsAsync("0",1000);
                foreach (var file in files.Entries)
                {
                    if (file.Name.Contains(fileName))
                    {
                        var res = await client.FilesManager.DownloadAsync(id: file.Id);
                        fileContents.Add(res);
                        Console.WriteLine(fileContents.Count);
                    }
                }
                Console.WriteLine("Publicaciones descargadas");
                return fileContents;
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al cargar las publicaciones\n\n" + ex.Message, 0);
                return null;
            }
            
        }


    }
}
