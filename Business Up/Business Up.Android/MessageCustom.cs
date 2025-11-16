using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Business_Up.Controls;
using Business_Up.Droid;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Xamarin.Forms;

[assembly: Dependency(typeof(MessageCustom))]
namespace Business_Up.Droid
{
    public class MessageCustom : IMensaje
    {
        public void ShowMessage(string mensaje, int duracion)
        {
            if(duracion > 0)
            {
                Toast.MakeText(Android.App.Application.Context, mensaje, ToastLength.Long).Show();
            }
            else
            {
                Toast.MakeText(Android.App.Application.Context, mensaje, ToastLength.Short).Show();
            }
        }
    }
}