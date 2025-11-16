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
    public partial class MisPublicaciones : ContentPage
    {
        public MisPublicaciones()
        {
            InitializeComponent();
            ListPublicaciones.BindingContext = new Contenido.ContenidoPublicaciones(true);
            ListPublicaciones.RefreshCommand = new Command(() => {
                Actualizar();
                ListPublicaciones.IsRefreshing = false;
            });
        }

        private void Actualizar()
        {
            ListPublicaciones.BindingContext = new Contenido.ContenidoPublicaciones(true);
        }

        private void buscartxt_TextChanged(object sender, TextChangedEventArgs e)
        {

        }

        private async void EliminarItem_Clicked(object sender, EventArgs e)
        {
            var editBoxButton = sender as Button;
            var result = await DisplayAlert("Eliminar publicacion", "¿Estas segur@?", "Si, estoy segur@", "Cancelar");
            if (editBoxButton != null && result)
            {
                var boxID = editBoxButton.CommandParameter as Entidades.Publicacion;
                Data.TListaPublicaciones.DeletePublicacion(Data.UsuarioData.UsuarioActual.Nombre, boxID.Titulo);
                Actualizar();
                _ = Data.TData.Publicar(null);
            }
        }

        private void ModificarCantidadItem_Clicked(object sender, EventArgs e)
        {

        }
    }
}