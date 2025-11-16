using Business_Up.Data;
using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Registro : ContentPage
    {
        public Registro()
        {
            InitializeComponent();
            contratxt.IsPassword = true;
        }

        private async void registro_Clicked(object sender, EventArgs e)
        {
            await Task.Run(Registrar);
        }

        private void Registrar()
        {
            if (!string.IsNullOrEmpty(usuariotext.Text) && !string.IsNullOrEmpty(contratxt.Text) && !string.IsNullOrEmpty(correotxt.Text))
            {
                Device.BeginInvokeOnMainThread(async () =>
                {
                    if (ValidacionCorreo())
                    {
                        progreso.IsRunning = true;
                        registro.IsEnabled = false;
                        var m = new MemoryStream();
                        await ObtenerImagenDefault().CopyToAsync(m);
                        var resul = await TData.Registro(new Usuario(usuariotext.Text, DataHandling.EncryptMD5(contratxt.Text), correotxt.Text, m.GetBuffer()));
                        if (resul != "Usuario ya existente" && !resul.Contains("Request timed out"))
                        {
                            progreso.IsRunning = false;
                            registro.IsEnabled = true;
                            _ = Application.Current.MainPage.Navigation.PopModalAsync();
                        }
                        else
                        {
                            if (resul == "Usuario ya existente")
                            {
                                progreso.IsRunning = false;
                                registro.IsEnabled = true;
                                MensajeUsuario.Text = "Usuario ya existente";
                                await Task.Delay(4000);
                                MensajeUsuario.Text = string.Empty;
                            }
                            else
                            {
                                progreso.IsRunning = false;
                                registro.IsEnabled = true;
                                MensajeUsuario.Text = "Error de conexion";
                                await Task.Delay(15000);
                                MensajeUsuario.Text = string.Empty;
                            }
                        }
                    }
                    else
                    {
                        MensajeUsuario.Text = "Ingrese un correo valido";
                        await Task.Delay(4000);
                        MensajeUsuario.Text = string.Empty;
                    }
                });
                
            }
            else
            {
                Device.BeginInvokeOnMainThread(async () =>
                {
                    MensajeUsuario.Text = "Rellene todos los campos";
                    await Task.Delay(4000);
                    MensajeUsuario.Text = string.Empty;
                });
            }
        }

        private Stream ObtenerImagenDefault()
        {
            var assembly = Assembly.GetExecutingAssembly();
            return assembly.GetManifestResourceStream("Business_Up.Contenido.iconoPerfil.png");
        }

        private bool ValidacionCorreo()
        {
            var email = correotxt.Text;
            var isValidEmail = Regex.IsMatch(email, @"^([\w\.\-]+)@([\w\-]+)((\.(\w){2,3})+)$");

            if (!isValidEmail)
            {
                correotxt.BackgroundColor = Color.Red;
                return false;
            }
            else
            {
                correotxt.BackgroundColor = Color.FromHex("#C8F9F8");
                return true;
            }
        }


    }
}