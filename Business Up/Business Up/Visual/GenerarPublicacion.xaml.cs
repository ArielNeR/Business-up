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
    public partial class GenerarPublicacion : ContentPage
    {
        private ImageSource _selectedImage;
        private byte[] datosImg;

        public GenerarPublicacion()
        {
            InitializeComponent();
        }

        private async void OnSelectButtonClicked(object sender, EventArgs e)
        {
            var file = await FilePicker.PickAsync();
            if (file != null)
            {
                _selectedImage = ImageSource.FromFile(file.FullPath);
                datosImg = File.ReadAllBytes(file.FullPath);
                PostImage.Source = _selectedImage;
            }
        }

        private void ButtonCancel_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PopModalAsync();
        }

        private async void ButtonShare_Clicked(object sender, EventArgs e)
        {
            if(!string.IsNullOrEmpty(titulo.Text) && !string.IsNullOrEmpty(descripcion.Text) && !(datosImg is null))
            {
                _ = Data.TData.Publicar(new Entidades.Publicacion(Data.UsuarioData.UsuarioActual, 20, datosImg, titulo.Text, descripcion.Text));
                await Application.Current.MainPage.Navigation.PopModalAsync();
            }else
                DependencyService.Get<IMensaje>().ShowMessage("Ingrese todos los datos requeridos", 0);
        }
    }
}