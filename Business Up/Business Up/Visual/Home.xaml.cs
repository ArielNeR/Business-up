using Business_Up.Contenido;
using Business_Up.Controls;
using Business_Up.Data;
using System;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Home : ContentPage
    {
        bool EsInicio = true;
        public Home()
        {
            InitializeComponent();
            Inicio();
        }

        private async void Inicio()
        {
            try
            {
                _ = TDataBox.CuentasSuspendidas();
                if (UsuarioData.UsuarioActual.Nombre.Equals("Ariel"))
                {
                    tipoUsuario.Text = "Usuario Administrador";
                    tipoUsuario.FontSize = 20;
                    tipoUsuario.FontAttributes = FontAttributes.Bold;
                    BotonAdmin.IsVisible = true;
                }
                ContenidoClientes.inicio = true;
                ContenidoInventario.inicio = true;
                vista.Refreshing += Vista_Refreshing;
                one.BindingContext = new ContenidoClientes("");
                two.BindingContext = new ContenidoInventario("");
                three.BindingContext = new ContenidoVentas("", 0);
                await Task.Delay(500);
                _ = Task.Run(MostrarDatos);
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Problemas con el manejo de datos:.\n\n" + ex.Message, 0);
            }
            
        }

        private void Vista_Refreshing(object sender, EventArgs e)
        {
            _ = Task.Run(MostrarDatos);
        }

        private void MostrarDatos()
        {
            try
            {
                Device.BeginInvokeOnMainThread(async () =>
                {
                    if (!EsInicio)
                        vista.IsRefreshing = true;
                    var resul = await TData.ImportarDatos();
                    if (resul == 0)
                    {
                        ContenidoClientes.inicio = true;
                        ContenidoInventario.inicio = true;
                        one.BindingContext = new ContenidoClientes("") { Estado = true };
                        two.BindingContext = new ContenidoInventario("") { Estado = true };
                        three.BindingContext = new ContenidoVentas("", 0) { Estado = true};
                    }
                    EsInicio = false;
                    vista.IsRefreshing = false;
                });
            }
            catch (Exception ex)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Problemas con el manejo de datos:.\n\n" + ex.Message, 0);
            }

        }

        private void Vender_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new Venta());
        }

        private void LimpiarSeleccion()
        {
            ListaClientes.SelectedItem = null;
        }

        private void ListaClientes_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            try
            {
                Entidades.Cliente item = e.CurrentSelection[0] as Entidades.Cliente;
                var VistaVenta = new Venta();
                VistaVenta.Datos(item);
                Application.Current.MainPage.Navigation.PushModalAsync(VistaVenta);
                VistaVenta.Disappearing += (sender2, e2) =>
                {
                    LimpiarSeleccion();
                };
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                //DependencyService.Get<IMensaje>().ShowMessage("Problemas con seleccion de los items:.\n\n" + ex.Message, 0);
            }
        }

        private void VerFactura_Clicked(object sender, EventArgs e)
        {
            var dato = (sender as Button).CommandParameter as Entidades.Venta;
            DependencyService.Get<PdfFactura>().AbrirFactura(dato.Factura);
            Application.Current.MainPage.Navigation.PushModalAsync(new VistaFactura());
        }

        private void BotonAdmin_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new ListaUsuariosSuspendidos());
        }
    }
}