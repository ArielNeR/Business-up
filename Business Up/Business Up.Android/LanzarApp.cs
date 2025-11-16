using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Business_Up.Droid;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Xamarin.Essentials;
using Xamarin.Forms;
using static Business_Up.Data.TData;

[assembly: Dependency(typeof(LanzarApp))]
namespace Business_Up.Droid
{
    public class LanzarApp : OpenApp
    {
        public void AbrirApp(string uri)
        {
            Launcher.OpenAsync("mailto:"+uri);
        }
    }
}