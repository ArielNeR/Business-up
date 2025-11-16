using Business_Up.Data;
using System;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Login : ContentPage
    {
        private int conteo = 0;
        public Login()
        {
            InitializeComponent();
            contratxt.IsPassword = true;

        }

        private async void ingreso_Clicked(object sender, EventArgs e)
        {
            await Task.Run(AccesoAsync);
        }

        private void AccesoAsync()
        {
            if (!string.IsNullOrEmpty(usuariotext.Text) && !string.IsNullOrEmpty(contratxt.Text))
            {
                Device.BeginInvokeOnMainThread(async () =>
                {
                    ingreso.IsEnabled = false;
                    progreso.IsRunning = true;
                    var cuentabloqueda = await TData.EsCuentaSuspendida(usuariotext.Text);
                    if (!cuentabloqueda)
                    {
                        var verificacion = await TDataBox.ExistUser(usuariotext.Text, DataHandling.EncryptMD5(contratxt.Text));
                        if (verificacion == 0)
                        {
                            UsuarioData.UsuarioActual = new Entidades.Usuario(usuariotext.Text, "", "", new byte[0]);
                            await TData.ImportarDatos();
                            progreso.IsRunning = false;
                            TData.GuardarDatosLocal();
                            Application.Current.MainPage = new AppShell();
                        }
                        else if (verificacion == 1)
                        {
                            progreso.IsRunning = false;
                            switch (conteo)
                            {
                                case 0:
                                    _ = DisplayAlert("Datos Incorrectos", "contraseña incorrecta", "Aceptar");
                                    conteo++;
                                    break;
                                case 1:
                                    _ = DisplayAlert("Datos Incorrectos", "contraseña incorrecta\nLe queda dos intentos", "Aceptar");
                                    conteo++;
                                    break;
                                case 2:
                                    _ = DisplayAlert("Datos Incorrectos", "contraseña incorrecta\nLe queda un intento", "Aceptar");
                                    conteo++;
                                    break;
                                case 3:
                                    _ = DisplayAlert("Datos Incorrectos", "contraseña incorrecta\nCuenta bloqueada temporalmente", "Aceptar");
                                    _ = TData.SuspenderCuenta(usuariotext.Text);
                                    conteo = 0;
                                    break;
                            }
                            
                        }
                        else if (verificacion == 2)
                        {
                            progreso.IsRunning = false;
                            _ = DisplayAlert("Datos Incorrectos", "Usuario no registrado", "Aceptar");
                        }
                        else if (verificacion == -1)
                        {
                            progreso.IsRunning = false;
                            _ = DisplayAlert("Sin acceso al servidor", "Dispositivo sin conexion", "Aceptar");
                        }
                    }
                    else
                    {
                        progreso.IsRunning = false;
                        _ = DisplayAlert("Cuenta suspendida", "Su cuenta esta suspendida temporalmente", "Aceptar");
                    }
                    ingreso.IsEnabled = true;
                });
            }
            else
            {
                _ = DisplayAlert("Ingreso de datos", "Rellene todos los campos", "Aceptar");
            }

        }

        private void registro_Clicked(object sender, EventArgs e)
        {
            var modalPage = new Registro();
            Application.Current.MainPage.Navigation.PushModalAsync(modalPage);
            modalPage.Disappearing += (sender2, e2) =>
            {
                //usuariotext.Text = modalPage.usuariotext.Text;
            };
        }
    }
}