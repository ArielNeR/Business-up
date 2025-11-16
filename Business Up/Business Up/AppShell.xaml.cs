using Business_Up.Controls;
using Business_Up.Visual;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class AppShell : Shell
    {
        Home inicio = new Home();
        Profile perfil = new Profile();
        Publicaciones publicaciones = new Publicaciones();
        Balance balance = new Balance();
        public AppShell()
        {
            InitializeComponent();
            Task.Run(RegisterContens);
        }

        void RegisterContens()
        {
            try
            {
                TabInicio.Content = inicio;
                TabAdministracion.Content = balance;
                publicaciones.ActualizarPublicaciones();
                TabPublicaciones.Content = publicaciones;
                TabPerfil.Content = perfil;
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Error:.\n\n" + ex.Message, 0);
            }
        }

        protected override bool OnBackButtonPressed()
        {
            Device.BeginInvokeOnMainThread(async() =>
            {
                var result = await DisplayAlert("Cierre de aplicacion", "¿Esta segur@ de salir?", "Si, deseo salir", "Cancelar");
                if (result)
                {
                    DependencyService.Get<ICloseApplication>().closeApplication();
                }
            });
            return true;
        }

        

    }
}