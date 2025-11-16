using Business_Up.Contenido;
using Business_Up.Controls;
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
    public partial class Clientes : ContentPage
    {
        private readonly Venta Venta;
        public Clientes(Venta venta)
        {
            InitializeComponent();
            Venta = venta;
            ViewClientes.BindingContext = new ContenidoClientes("");
            ViewClientes.RefreshCommand = new Command(() => {
                Actualizar();
                ViewClientes.IsRefreshing = false;
            });
        }

        private void Actualizar()
        {
            ViewClientes.BindingContext = new ContenidoClientes(buscartxt.Text);
        }

        private void ViewClientes_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            if(!(Venta is null))
            {
                var datos = ((Entidades.Cliente)e.Item);
                Application.Current.MainPage.Navigation.PopModalAsync();
                Venta.Datos(TlistaClientes.BuscarCliente(datos.Nombre));
                DependencyService.Get<IMensaje>().ShowMessage("Cliente Seleccionado", 0);
            }
        }

        private void Agregar_Clicked(object sender, EventArgs e)
        {
            var modalPage = new AgregarCliente(null);
            Application.Current.MainPage.Navigation.PushModalAsync(modalPage);
            buttonAgregar.IsEnabled = false;
            modalPage.Disappearing += (sender2, e2) =>
            {
                Actualizar();
                buttonAgregar.IsEnabled = true;
            };
        }

        private void buscartxt_TextChanged(object sender, TextChangedEventArgs e)
        {
            Actualizar();
        }

        private void Atras_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PopModalAsync();
        }

        private void Aceptar_Clicked(object sender, EventArgs e)
        {
            DependencyService.Get<IMensaje>().ShowMessage("Cliente Agregado", 0);
        }

        private async void EliminarItem_Clicked(object sender, EventArgs e)
        {
            var editBoxButton = sender as Button;
            var result = await DisplayAlert("Quitar item de la lista de clientes", "¿Estas segur@?", "Si, estoy segur@", "Cancelar");
            if (editBoxButton != null && result)
            {
                var boxID = editBoxButton.CommandParameter as Entidades.Cliente;
                for (int i = 0; i < TlistaClientes.ListaClientes.Count; i++)
                {
                    if (TlistaClientes.ListaClientes[i].Nombre == boxID.Nombre)
                    {
                        TlistaClientes.ListaClientes.Remove(TlistaClientes.ListaClientes[i]);
                        break;
                    }
                }
                Actualizar();
                _ = Task.Run(TData.GuardarDatos);
            }
        }

        private async void ModificarCantidadItem_Clicked(object sender, EventArgs e)
        {
            var editBoxButton = sender as Button;
            var boxID = editBoxButton.CommandParameter as Entidades.Cliente;
            var modalPage = new AgregarCliente(boxID);
            await Application.Current.MainPage.Navigation.PushModalAsync(modalPage);
        }
    }
}