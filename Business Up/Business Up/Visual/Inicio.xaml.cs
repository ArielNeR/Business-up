using Business_Up.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Inicio : ContentPage
    {
        public Inicio()
        {
            InitializeComponent();
            Task.Run(animacion);
        }

        /// <summary>
        /// Este método realiza una subproceso de animacion mientras importa los datos guardados o en lugar de no tener se procede al apartado de login.
        /// </summary>
        private void animacion()
        {
            Device.BeginInvokeOnMainThread(async () =>
            {
                Logo.Opacity = 0;
                var resul = TData.ImportarDatosLocal();
                await Logo.FadeTo(1, 1000);
                if (resul == 0 && !string.IsNullOrEmpty(UsuarioData.UsuarioActual.Nombre))
                {
                    Application.Current.MainPage = new AppShell();
                    //DatosApp();
                }
                else
                {
                    Application.Current.MainPage = new Login();
                }
            });
            
        }

        private void DatosApp()
        {
            Device.BeginInvokeOnMainThread(async () =>
            {
                await TData.ObtenerPublicaciones();
            });
        }
    }
}