using Microcharts;
using SkiaSharp;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.CommunityToolkit.Extensions;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Balance : ContentPage
    {
        List<ChartEntry> Gventas = new List<ChartEntry> { };
        List<ChartEntry> Gganancias = new List<ChartEntry> { };


        public Balance()
        {
            InitializeComponent();
            vista.Refreshing += Vista_Refreshing;
            Task.Run(Iniciar);
        }

        private void Vista_Refreshing(object sender, EventArgs e)
        {
            Iniciar();
            vista.IsRefreshing = false;
        }

        /**
        Método que se encarga de iniciar la vista del dashboard. Este método obtiene los datos necesarios para
        llenar la información en la vista y crear los gráficos correspondientes.
        @return No retorna ningún valor.
        */
        private void Iniciar()
        {
            Device.BeginInvokeOnMainThread(async () =>
            {
                var resul = await Data.TData.ImportarDatos();
                Gventas.Clear();
                Gganancias.Clear();
                tipoBalance.Text = "Diario";
                double totalPediente = 0;
                int cantidadPagado = 0;
                double totInventario = 0;
                double totalGanancias = 0;
                foreach (var item in Data.TListaVentas.ListaVentas)
                {
                    if (!item.Pagado)
                    {
                        totalPediente += item.Total;
                    }
                    else
                    {
                        Gventas.Add(new ChartEntry((float)item.Total)
                        {
                            Color = SKColor.Parse("#FF1943"),
                            Label = item.Cliente.Nombre,
                            ValueLabel = $"${item.Total}"
                        });
                        cantidadPagado++;
                    }
                }
                totInventario += Data.TlistaInventario.Inventario.Sum(x => x.Precio * x.Cantidad);
                CuentasPendientes.Text = $"${totalPediente}";
                NumeroVentas.Text = cantidadPagado.ToString();
                totalInventario.Text = totInventario.ToString();
                foreach (var item in Data.TListaVentas.ListaVentas)
                {
                    if (item.DatoFecha.Equals(Fechabtn.Date.ToString("d")))
                    {
                        totalGanancias += item.Total;
                        Gganancias.Add(new ChartEntry((float)item.Total)
                        {
                            Color = SKColor.Parse("#FF1943"),
                            Label = item.Cliente.Nombre,
                            ValueLabel = $"${item.Total}"
                        });
                    }
                }
                Ganancias.Text = totalGanancias.ToString();
                GraficoVentas.Chart = new LineChart() { Entries = Gventas };
                GraficoGanancias.Chart = new LineChart() { Entries = Gganancias };
            });
        }

        private async void TipoBalance_Clicked(object sender, EventArgs e)
        {
            _ = await Shell.Current.Navigation.ShowPopupAsync(new TipoBalance(this) { Size = new Size(200, 300)});
        }


        public void Tipo(string tipo)
        {
            tipoBalance.Text = tipo;
        }

        private void CuentasPorCobrar_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new ListaDeVentas(1));
        }

        private void Ventas_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new ListaDeVentas(2));
        }

        private void Inventario_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PushModalAsync(new Inventario(null));
        }

        private void Fechabtn_DateSelected(object sender, DateChangedEventArgs e)
        {
            Task.Run(Iniciar);
        }
    }
}