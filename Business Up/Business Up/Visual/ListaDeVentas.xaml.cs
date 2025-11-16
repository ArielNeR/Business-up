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
    public partial class ListaDeVentas : ContentPage
    {
        private int estado = 2;
        public ListaDeVentas(int estado)
        {
            InitializeComponent();
            ViewVentas.BindingContext = new Contenido.ContenidoVentas("", estado);
            this.estado = estado;
            ViewVentas.RefreshCommand = new Command(() => {
                Actualizar();
                ViewVentas.IsRefreshing = false;
            });
        }

        private void Actualizar()
        {
            ViewVentas.BindingContext = new Contenido.ContenidoVentas(buscartxt.Text, estado);
        }

        private void buscartxt_TextChanged(object sender, TextChangedEventArgs e)
        {
            Actualizar();
        }

        private void VerFactura_Clicked(object sender, EventArgs e)
        {
            var dato = (sender as Button).CommandParameter as Entidades.Venta;
            DependencyService.Get<PdfFactura>().AbrirFactura(dato.Factura);
            Application.Current.MainPage.Navigation.PushModalAsync(new VistaFactura());
        }

        private async void ViewVentas_ItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            var result = await DisplayAlert("Establecer esta venta como concluida/pagada", "¿Estas segur@?", "Si, estoy segur@", "Cancelar");
            if (result)
            {
                var item = e.SelectedItem as Entidades.Venta;
                TListaVentas.ObtenerVenta(item.NumeroFactura).Pagado = true;
                _ = TData.GuardarDatos();
                Actualizar();
            }
        }
    }
}