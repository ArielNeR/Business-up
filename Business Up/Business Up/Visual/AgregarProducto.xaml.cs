using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class AgregarProducto : ContentPage
    {
        Thread hilo;
        private bool EstadoModificacion = false;

        public AgregarProducto(Entidades.Inventario inv)
        {
            InitializeComponent();
            unidadMedida.ItemsSource = new List<string> { "Unidad", "Metro", "Centimetro", "Litros", "Kilogramos" , "Gramos"};
            unidadMedida.SelectedIndex = 0;
            cerrarNotificacion.Text = "\u2192";
            Mensaje.TranslateTo(-300, 80, 0);
            if(!(inv is null))
                Editar(inv);
        }

        /// <summary>
        /// Método que se encarga de mostrar los datos de un cliente en la interfaz gráfica de usuario para permitir su edición.
        /// </summary>
        /// <param name="inventario">Objeto de la clase Inventario con los datos del inventario a editar.</param>
        private void Editar(Entidades.Inventario inventario)
        {
            Titulo.Text = "Modificar Producto";
            var item = Data.TlistaInventario.BuscarItem(inventario.Nombre) as Entidades.Producto;
            nombretxt.Text = item.Nombre;
            precioVentatxt.Text = item.PrecioVenta.ToString();
            Agregar.CommandParameter = item.Nombre;
            precioProveedortxt.Text = item.PrecioProveedor.ToString();
            cantidadtxt.Text = item.CantidadTotal.ToString();
            codigoBarratxt.Text = item.CodigoBarra;
            EstadoModificacion = true;
        }

        private void BotonAtras_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PopModalAsync();
        }

        /**
        Método que se ejecuta cuando se hace clic en el botón "Agregar" de la página de edición de un producto en el inventario.
        Si la validación de los campos es exitosa, se realiza la modificación o ingreso del producto en el inventario y se guarda la información local y en la nube.
        En caso contrario, se muestra un mensaje de error al usuario.
        @param sender Objeto que envía el evento.
        @param e Evento que se dispara al hacer clic en el botón "Agregar".
        */
        private async void Agregar_Clicked(object sender, EventArgs e)
        {
            if (Validacion())
            {
                if (EstadoModificacion)
                {
                    try
                    {
                        var res = Data.TlistaInventario.BuscarItem(Agregar.CommandParameter.ToString()) as Entidades.Producto;
                        res.Nombre = nombretxt.Text;
                        res.PrecioVenta = Convert.ToDouble(precioVentatxt.Text);
                        res.PrecioProveedor = Convert.ToDouble(precioProveedortxt.Text);
                        res.UnidadMedida = unidadMedida.SelectedItem.ToString();
                        res.CantidadTotal = Convert.ToInt32(cantidadtxt.Text);
                        res.CodigoBarra = codigoBarratxt.Text;
                        var res2 = Data.TlistaInventario.GetItem(Agregar.CommandParameter.ToString());
                        res2.Nombre = res.Nombre;
                        res2.Precio = res.PrecioVenta;
                        res2.UnidadMedida = res.UnidadMedida;
                        res2.Cantidad = res.CantidadTotal;
                        res2.CodigoBarra = res.CodigoBarra;
                        Data.TData.GuardarDatosLocal();
                        await Application.Current.MainPage.Navigation.PopModalAsync();
                        await Task.Run(Data.TData.GuardarDatos);
                    }
                    catch (Exception ex)
                    {
                        Agregar.IsEnabled = false;
                        var tag = (string)cerrarNotificacion.CommandParameter;
                        if (tag is null || tag.Equals("false"))
                        {
                            cerrarNotificacion.CommandParameter = "true";
                            MensajeTexto.Text = $"Error: {ex.Message}";
                            await Mensaje.LayoutTo(new Rectangle(Mensaje.X + 320, Mensaje.Y, Mensaje.Width, Mensaje.Height), 600);
                            hilo = new Thread(mostrarMensaje);
                            hilo.Start();
                        }
                    }
                    
                }
                else
                {
                    var producto = new Entidades.Producto(nombretxt.Text,
                    unidadMedida.SelectedItem.ToString(), Convert.ToDouble(precioVentatxt.Text),
                    Convert.ToDouble(precioProveedortxt.Text), Convert.ToInt32(cantidadtxt.Text), codigoBarratxt.Text);
                    Data.TlistaInventario.IngresarInventario(producto);
                    Data.TData.GuardarDatosLocal();
                    await Application.Current.MainPage.Navigation.PopModalAsync();
                    await Task.Run(Data.TData.GuardarDatos);
                }
            }
            else
            {
                Agregar.IsEnabled = false;
                var tag = (string)cerrarNotificacion.CommandParameter;
                if (tag is null || tag.Equals("false"))
                {
                    cerrarNotificacion.CommandParameter = "true";
                    MensajeTexto.Text = "Debe rellenar todos los campos";
                    await Mensaje.LayoutTo(new Rectangle(Mensaje.X + 320, Mensaje.Y, Mensaje.Width, Mensaje.Height), 600);
                    hilo = new Thread(mostrarMensaje);
                    hilo.Start();
                }
            }
            
        }

        /**
        Muestra un mensaje en pantalla durante un tiempo determinado.
        Después de pasado el tiempo, mueve el mensaje fuera de la pantalla y habilita
        el botón de Agregar.
        También utiliza un hilo para mostrar el mensaje en segundo plano.
        */
        private async void mostrarMensaje()
        {
            await Task.Delay(4000);
            Device.BeginInvokeOnMainThread(async () =>
            {
                await Mensaje.LayoutTo(new Rectangle(Mensaje.X - 320, Mensaje.Y, Mensaje.Width, Mensaje.Height), 400);
                Agregar.IsEnabled = true;
            });
            cerrarNotificacion.CommandParameter = "false";
            hilo.Abort();
        }

        private bool Validacion()
        {
            if (!string.IsNullOrEmpty(nombretxt.Text) && !string.IsNullOrEmpty(precioVentatxt.Text) && !string.IsNullOrEmpty(precioProveedortxt.Text) && !string.IsNullOrEmpty(cantidadtxt.Text) && !string.IsNullOrEmpty(codigoBarratxt.Text))
                return true;
            else
                return false;
        }

    }
}