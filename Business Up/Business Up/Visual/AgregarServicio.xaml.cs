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
    public partial class AgregarServicio : ContentPage
    {
        Thread hilo;
        private bool EstadoModificacion = false;

        public AgregarServicio(Entidades.Inventario ser)
        {
            InitializeComponent();
            cerrarNotificacion.Text = "\u2192";
            Mensaje.TranslateTo(-300, 100, 0);
            if(!(ser is null))
                Editar(ser);
        }

        private void Editar(Entidades.Inventario inventario)
        {
            Titulo.Text = "Modificar Servicio";
            var item = Data.TlistaInventario.BuscarItem(inventario.Nombre) as Entidades.Servicio;
            nombretxt.Text = item.Nombre;
            preciotxt.Text = item.Valor.ToString();
            Agregar.CommandParameter = item.Nombre;
            codigoBarratxt.Text = item.CodigoBarra;
            EstadoModificacion = true;
        }

        private void BotonAtras_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PopModalAsync();
        }

        private async void Agregar_Clicked(object sender, EventArgs e)
        {
            if (Validacion())
            {
                if (EstadoModificacion)
                {
                    var res = Data.TlistaInventario.BuscarItem(Agregar.CommandParameter.ToString()) as Entidades.Servicio;
                    res.Nombre = nombretxt.Text;
                    res.Valor = Convert.ToDouble(preciotxt.Text);
                    res.CodigoBarra = codigoBarratxt.Text;
                    var res2 = Data.TlistaInventario.GetItem(Agregar.CommandParameter.ToString());
                    res2.Nombre = res.Nombre;
                    res2.Precio = res.Valor;
                    res2.CodigoBarra = res.CodigoBarra;
                    Data.TData.GuardarDatosLocal();
                    await Application.Current.MainPage.Navigation.PopModalAsync();
                    await Task.Run(Data.TData.GuardarDatos);
                }
                else
                {
                    var servicio = new Entidades.Servicio(nombretxt.Text, Convert.ToDouble(preciotxt.Text),
                    codigoBarratxt.Text);
                    Data.TlistaInventario.IngresarInventario(servicio);
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
            if (!string.IsNullOrEmpty(nombretxt.Text) && !string.IsNullOrEmpty(preciotxt.Text) && !string.IsNullOrEmpty(codigoBarratxt.Text))
                return true;
            else
                return false;
        }

    }
}