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
    public partial class PaginaContacto : ContentPage
    {
        private Thread hilo;

        public PaginaContacto()
        {
            InitializeComponent();
            Mensaje.TranslateTo(-300, -160, 0);
        }

        private async void BotonEnviar_Clicked(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(txtComment.Text))
            {
                BotonEnviar.IsEnabled = false;
                await Mensaje.LayoutTo(new Rectangle(Mensaje.X + 340, Mensaje.Y, Mensaje.Width, Mensaje.Height), 600);
                hilo = new Thread(mostrarMensaje);
                hilo.Start();
                _ = Data.TData.AgregarComentarioSugerencia(txtComment.Text);
            }
        }

        private async void mostrarMensaje()
        {
            await Task.Delay(4000);
            Device.BeginInvokeOnMainThread(async () =>
            {
                await Mensaje.LayoutTo(new Rectangle(Mensaje.X - 340, Mensaje.Y, Mensaje.Width, Mensaje.Height), 400);
                await Application.Current.MainPage.Navigation.PopModalAsync();
            });
            hilo.Abort();
        }

    }
}