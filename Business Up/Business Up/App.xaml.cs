using Business_Up.Controls;
using Business_Up.Visual;
using System;
using Xamarin.CommunityToolkit.UI.Views;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up
{
    public partial class App : Application
    {
        public App()
        {
            InitializeComponent();
            MainPage = new Inicio();
            //MainPage = new ListaUsuariosSuspendidos();
        }

        protected override void OnStart()
        {
        }

        protected override void OnSleep()
        {
        }

        protected override void OnResume()
        {
        }
    }
}
