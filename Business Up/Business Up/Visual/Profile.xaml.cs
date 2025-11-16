using Business_Up.Controls;
using Business_Up.Entidades;
using System;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Timers;
using Xamarin.CommunityToolkit.UI.Views;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Profile : ContentPage
    {
        public Profile()
        {
            InitializeComponent();
            Inicio();
        }

        public void Inicio()
        {
            ProfilePicture.Source = ImageSource.FromStream(() => new MemoryStream(Data.UsuarioData.UsuarioActual.FotoPerfil));
            usuariotxt.Text = Data.UsuarioData.UsuarioActual.Nombre;
            correotxt.Text = Data.UsuarioData.UsuarioActual.Correo;
        }

        private void CerrarSesion_Clicked(object sender, EventArgs e)
        {
            Data.TData.BorrarDatos();
            Application.Current.MainPage = new Login();
        }

        private void produtos_servicios_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new Inventario(null));
        }

        private void clientes_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new Clientes(null));
        }

        private void ventas_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new ListaDeVentas(2));
        }

        private void mispublicaciones_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new MisPublicaciones());
        }

        private void publicar_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new GenerarPublicacion());
        }

        private void contactanos_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new PaginaContacto());
        }

        private void btnEditarPerfil_Clicked(object sender, EventArgs e)
        {
            var modal = new EditarInformacionPerfil();
            Application.Current.MainPage.Navigation.PushModalAsync(modal);
            modal.Disappearing += async (sender2, e2) =>
            {
                Inicio();
                await Data.TData.GuardarDatos();
            };
        }

    }



}
