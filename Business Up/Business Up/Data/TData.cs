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
    public class TData
    {
        public interface OpenApp { void AbrirApp(string uri); }
        public static bool actualizando = false;

        /// <summary>
        /// Método estático y asíncrono que permite guardar datos en la nube.
        /// Los datos son serializados utilizando BinaryFormatter y subidos al servidor a través de la clase TDataBox.
        /// </summary>
        /// <exception cref="Exception">Lanza una excepción en caso de error al guardar los datos ya sea por coexion o tipo de datos.</exception>
        public static async Task GuardarDatos()
        {
            try
            {
                BinaryFormatter BF = new BinaryFormatter();
                List<object> listas = new List<object>
                {
                    TlistaClientes.ListaClientes,
                    TlistaInventario.Inventario,
                    TlistaInventario.ListaProductos,
                    TlistaInventario.ListaServicios,
                    UsuarioData.UsuarioActual,
                    TListaVentas.ListaVentas
                };
                using (MemoryStream ms = new MemoryStream())
                {
                    BF.Serialize(ms, listas);
                    await TDataBox.UploadAsync(ms,UsuarioData.UsuarioActual.Nombre,0);
                    ms.Close();
                }
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al guardar.\n\n" + ex.Message, 0);
            }

        }

        /// <summary>
        /// Método estático y asíncrono que permite importar datos de la nube.
        /// Los datos se descargan utilizando la clase TDataBox y se deserializan con BinaryFormatter.
        /// </summary>
        /// <returns>
        /// Devuelve un entero que indica si la importación de datos se realizó con éxito:
        /// 0 = éxito, 1 = error.
        /// </returns>
        /// <exception cref="Exception">Lanza una excepción en caso de error al importar los datos.</exception>
        public static async Task<int> ImportarDatos()
        {
            try
            {
                BinaryFormatter BF = new BinaryFormatter();
                var data = await TDataBox.DownloadAsync(UsuarioData.UsuarioActual.Nombre);
                List<object> DatosCargados = (List<object>)BF.Deserialize(data);
                TlistaClientes.ListaClientes = (List<Cliente>)DatosCargados.ElementAt(0);
                TlistaInventario.Inventario = (List<Inventario>)DatosCargados.ElementAt(1);
                TlistaInventario.ListaProductos = (List<Producto>)DatosCargados.ElementAt(2);
                TlistaInventario.ListaServicios = (List<Servicio>)DatosCargados.ElementAt(3);
                UsuarioData.UsuarioActual = (Usuario)DatosCargados.ElementAt(4);
                TListaVentas.ListaVentas = (List<Venta>)DatosCargados.ElementAt(5);
                return 0;
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al importar datos del servidor.\n\n" + ex.Message, 0);
                return 1;
            }
        }

        /// <summary>
        /// Agrega una publicación a una lista de publicaciones y la sube al servidor mediante la clase TDataBox.
        /// Si la publicación ya existe, se muestra un mensaje indicando el problema. En caso de que se ingrese un objeto nulo,
        /// se sube la lista de publicaciones sin agregar nada. El método maneja excepciones y muestra mensajes de error en caso de que ocurran.
        /// </summary>
        /// <param name="pb">Objeto de tipo Publicacion que se desea agregar a la lista de publicaciones</param>
        /// <returns>Un objeto Task que representa la tarea asincrónica realizada por el método. Devuelve 0 si la operación se realizó correctamente y 1 en caso contrario.</returns>

        public static async Task Publicar(Publicacion pb)
        {
            try
            {
                if(!(pb is null))
                {
                    BinaryFormatter BF = new BinaryFormatter();
                    var resul = TListaPublicaciones.Publicar(pb);
                    if (resul)
                    {
                        using (MemoryStream ms = new MemoryStream())
                        {
                            BF.Serialize(ms, TListaPublicaciones.ListaPublicaciones);
                            await TDataBox.UploadAsyncPublicacion(ms);
                            ms.Close();
                        }
                    }
                    else
                        DependencyService.Get<IMensaje>().ShowMessage("Publicación existente\nIngrese otro título", 0);
                }
                else
                {
                    BinaryFormatter BF = new BinaryFormatter();
                    using (MemoryStream ms = new MemoryStream())
                    {
                        BF.Serialize(ms, TListaPublicaciones.ListaPublicaciones);
                        await TDataBox.UploadAsyncPublicacion(ms);
                        ms.Close();
                    }
                }
                
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al publicar.\n\n" + ex.Message, 0);
            }

        }

        /// <summary>
        /// Agrega un comentario a una sugerencia y lo sube al servidor mediante la clase TDataBox.
        /// Se utiliza la clase PdfFactura para generar el comentario en formato PDF. El método maneja excepciones
        /// y muestra mensajes de error en caso de que ocurran.
        /// </summary>
        /// <param name="comentario">Cadena de texto que representa el comentario a agregar</param>
        /// <returns>Un objeto Task que representa la tarea asincrónica realizada por el método.</returns>

        public static async Task AgregarComentarioSugerencia(string comentario)
        {
            try
            {
                BinaryFormatter BF = new BinaryFormatter();
                var DC = DependencyService.Get<PdfFactura>().GenerarComentario(comentario);
                using (MemoryStream ms = new MemoryStream(DC))
                {
                    await TDataBox.UploadAsyncComentario(ms);
                    ms.Close();
                }
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al comentar.\n\n" + ex.Message, 0);
            }

        }

        /// <summary>
        /// Método asincrónico que obtiene las publicaciones almacenadas en el servidor y las agrega a la lista de publicaciones de la aplicación.
        /// </summary>
        /// <returns>0 si se completó la obtención de las publicaciones correctamente, 1 si hubo algún error en la conexión con el servidor.</returns>
        public static async Task<int> ObtenerPublicaciones()
        {
            try
            {
                BinaryFormatter BF = new BinaryFormatter();
                var data = await TDataBox.DownloadAsyncPublicacion();
                foreach (var item in data)
                {
                    List<Publicacion> DatosCargados = (List<Publicacion>)BF.Deserialize(item);
                    if (DatosCargados.Count > 0 && !(DatosCargados is null))
                    {
                        foreach (var dat in DatosCargados)
                        {
                            TListaPublicaciones.Publicar(dat);
                        }
                    }
                    else if(DatosCargados is null)
                    {
                        TListaPublicaciones.ListaPublicaciones = new List<Publicacion>();
                    }
                }
                return 0;

            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al conectar con el servidor.\n\n" + ex.Message, 0);
                return 1;
            }
        }

        /// <summary>
        /// Método que realiza el registro de un usuario y sube la información necesaria a un servicio de almacenamiento en la nube.
        /// </summary>
        /// <param name="user">Objeto de tipo Usuario con los datos del usuario a registrar.</param>
        /// <returns>Un string con un mensaje indicando el éxito o el error del registro.</returns>
        public static async Task<string> Registro(Usuario user)
        {
            try
            {
                BinaryFormatter BF = new BinaryFormatter();
                string resul = "";
                List<object> listas = new List<object>
                {
                    new List<Cliente>(),
                    new List<Inventario>(),
                    new List<Producto>(),
                    new List<Servicio>(),
                    user,
                    new List<Venta>()
                };
                using (MemoryStream ms = new MemoryStream())
                {
                    BF.Serialize(ms, listas);
                    resul = await TDataBox.UploadAsync(ms, user.Nombre, 1);
                    ms.Close();
                }
                return resul;
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al registrar:.\n\n" + ex.Message, 0);
                return "Error: " + ex.Message;
            }
        }

        /// <summary>
        /// Guarda los datos locales de la aplicación en un archivo binario.
        /// Se guardan las listas de clientes, inventario, productos, servicios, ventas y el usuario actual.
        /// </summary>
        public static void GuardarDatosLocal()
        {
            try
            {
                BinaryFormatter BF = new BinaryFormatter();
                Console.WriteLine(UsuarioData.UsuarioActual.Nombre);
                List<object> listas = new List<object>()
                {
                    TlistaClientes.ListaClientes,
                    TlistaInventario.Inventario,
                    TlistaInventario.ListaProductos,
                    TlistaInventario.ListaServicios,
                    UsuarioData.UsuarioActual,
                    TListaVentas.ListaVentas
                };
                string ruta = Environment.GetFolderPath(Environment.SpecialFolder.Personal) + "/dataComplex.save";
                if (File.Exists(ruta))
                    File.Delete(ruta);
                using (FileStream fileStream = new FileStream(ruta, FileMode.OpenOrCreate))
                {
                    BF.Serialize(fileStream, listas);
                    fileStream.Close();
                }
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al guardar.\n\n" + ex.Message, 0);
            }

        }

        /**<summary>
         Método para borrar los datos guardados localmente en el dispositivo.
        </summary>
        <remarks>
        Busca el archivo "dataComplex.save" en la carpeta personal del dispositivo y lo borra si existe.
        </remarks>
        <exception cref="Exception">Lanza una excepción si ocurre un error al borrar el archivo.</exception>
        */
        public static void BorrarDatos()
        {
            try
            {
                string ruta = Environment.GetFolderPath(Environment.SpecialFolder.Personal) + "/dataComplex.save";
                if (File.Exists(ruta))
                    File.Delete(ruta);
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al guardar.\n\n" + ex.Message, 0);
            }
        }

        /// <summary>
        /// Importa los datos almacenados localmente en formato binario.
        /// Si se encuentran datos almacenados, los carga en las listas de la aplicación.
        /// Si no se encuentran datos almacenados, devuelve un código de error 1.
        /// Si ocurre un error durante la operación, devuelve un código de error 2 y muestra un mensaje de error.
        /// </summary>
        /// <returns>
        /// Código de error 0 si se han importado los datos correctamente.
        /// Código de error 1 si no se han encontrado datos almacenados.
        /// Código de error 2 si se ha producido un error durante la operación.
        /// </returns>
        public static int ImportarDatosLocal()
        {
            try
            {
                string ruta = Environment.GetFolderPath(Environment.SpecialFolder.Personal) + "/dataComplex.save";
                if (File.Exists(ruta))
                {
                    BinaryFormatter BF = new BinaryFormatter();
                    FileStream Archivo = File.Open(ruta, FileMode.Open);
                    List<object> DatosCargados = (List<object>)BF.Deserialize(Archivo);
                    Archivo.Close();
                    TlistaClientes.ListaClientes = (List<Cliente>)DatosCargados.ElementAt(0);
                    TlistaInventario.Inventario = (List<Inventario>)DatosCargados.ElementAt(1);
                    TlistaInventario.ListaProductos = (List<Producto>)DatosCargados.ElementAt(2);
                    TlistaInventario.ListaServicios = (List<Servicio>)DatosCargados.ElementAt(3);
                    UsuarioData.UsuarioActual = (Usuario)DatosCargados.ElementAt(4);
                    TListaVentas.ListaVentas = (List<Venta>)DatosCargados.ElementAt(5);
                    return 0;
                }
                else
                    return 1;
                

            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error al importar datos:\n\n" + ex.Message, 0);

                return 2;
            }
        }


        /**
        <summary>
        Verifica si una cuenta se encuentra suspendida, es decir, si existe un archivo con su nombre en la lista de cuentas suspendidas.
        </summary>
        <param name="NombreCuenta">El nombre de la cuenta que se desea verificar.</param>
        <returns>Devuelve un valor booleano que indica si la cuenta se encuentra suspendida o no.</returns>
        <remarks>
        Este método utiliza una lista de cuentas suspendidas proporcionada por TDataBox.CuentasSuspendidas() para determinar si la cuenta dada está suspendida.
        </remarks>
        */
        public static async Task<bool> EsCuentaSuspendida(string NombreCuenta)
        {
            bool resul = false;
            foreach (var item in await TDataBox.CuentasSuspendidas())
            {
                if (item.Name.Equals(NombreCuenta + ".save"))
                {
                    return true;
                }
            }
            return resul;
        }

        /// <summary>
        /// Obtiene la lista de cuentas suspendidas.
        /// </summary>
        /// <returns>Una lista de cadenas de texto que representan los nombres de las cuentas suspendidas.</returns>
        public static async Task<List<string>> ListaCuentaSuspendidas()
        {
            try
            {
                List<string> resul = new List<string>();
                foreach (var item in await TDataBox.CuentasSuspendidas())
                {
                    var aux = item.Name.Replace(".save", "");
                    resul.Add(aux);
                }
                return resul;
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error interpretar los datos:\n\n" + ex.Message, 0);
                return new List<string> { "Error" };
            }
            
        }

        /**
        SuspenderCuenta - Método estático que permite suspender una cuenta específica.
        @param NombreCuenta: nombre de la cuenta a suspender.
        @return Task: tarea asincrónica que indica cuando la cuenta ha sido suspendida.
        @throws Exception: excepción genérica en caso de fallar al suspender la cuenta.
        */
        public static async Task SuspenderCuenta(string NombreCuenta)
        {
            await TDataBox.BloquearCuenta(NombreCuenta);
        }

        /**
        Desbloquea una cuenta suspendida y la habilita para su uso.
        @param NombreCuenta Nombre de la cuenta a desbloquear.
        @return Una tarea que representa la operación asincrónica de desbloqueo de cuenta.
        @throws Exception Si ocurre un error al desbloquear la cuenta.
        */
        public static async Task DesbloquearCuenta(string NombreCuenta)
        {
            await TDataBox.DesbloquearCuentaSuspendida(NombreCuenta);
        }

    }
}
