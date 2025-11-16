using Business_Up.Controls;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class EditarInformacionPerfil : ContentPage
    {
        private ImageSource _selectedImage;
        private byte[] datosImg;
        public EditarInformacionPerfil()
        {
            InitializeComponent();
            Inicio();
        }

        private void Inicio()
        {
            var dat = Data.UsuarioData.UsuarioActual;
            txtNombre.IsEnabled = false;
            imgPerfil.Source = ImageSource.FromStream(() => new MemoryStream(dat.FotoPerfil));
            txtNombre.Text = dat.Nombre;
            txtCorreo.Text = dat.Correo;
        }

        private async void btnEditarFoto_Clicked(object sender, EventArgs e)
        {
            var file = await FilePicker.PickAsync();
            if (file != null)
            {
                _selectedImage = ImageSource.FromFile(file.FullPath);
                datosImg = File.ReadAllBytes(file.FullPath);
                imgPerfil.Source = _selectedImage;
            }
        }

        private void btnGuardar_Clicked(object sender, EventArgs e)
        {
            Guardar();
        }

        private async void Guardar()
        {
            Data.UsuarioData.UsuarioActual.FotoPerfil = datosImg;
            Data.UsuarioData.UsuarioActual.Nombre = txtNombre.Text;
            Data.UsuarioData.UsuarioActual.Correo = txtCorreo.Text;
            Data.TData.GuardarDatosLocal();
            await Application.Current.MainPage.Navigation.PopModalAsync();
        }

        private async void btnAtras_Clicked(object sender, EventArgs e)
        {
            await Application.Current.MainPage.Navigation.PopModalAsync();
        }
    }
}