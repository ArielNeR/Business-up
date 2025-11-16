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
    public partial class ListaUsuariosSuspendidos : ContentPage
    {
        public ListaUsuariosSuspendidos()
        {
            InitializeComponent();
            ListaCuentas.Refreshing += ListaCuentas_Refreshing;
            Task.Run(Actualizar);
        }

        private void Actualizar()
        {
            Device.BeginInvokeOnMainThread(async () =>
            {
                ListaCuentas.ItemsSource = await Data.TData.ListaCuentaSuspendidas();
                ListaCuentas.IsRefreshing = false;
            });
        }

        private void ListaCuentas_Refreshing(object sender, EventArgs e)
        {
            Actualizar();
        }

        private async void EliminarItem_Clicked(object sender, EventArgs e)
        {
            var boton = sender as Button;
            await Data.TData.DesbloquearCuenta(boton.CommandParameter.ToString());
            Actualizar();
        }

        private void buttonAceptar_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PopModalAsync();
        }
    }
}