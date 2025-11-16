using Business_Up.Data;
using Business_Up.Visual;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Timers;
using Xamarin.CommunityToolkit.UI.Views;
using Xamarin.Essentials;
using Xamarin.Forms;

namespace Business_Up.Controls
{
    public class BaseViewModel : INotifyPropertyChanged
    {


        public event PropertyChangedEventHandler PropertyChanged;

        public bool Estado = false;

        string latestRace;
        public Task Init { get; set; }

        public string LatestRace
        {
            get
            {
                return latestRace;
            }
            set
            {
                latestRace = value;
            }
        }

        public LayoutState GetState
        {
            get
            {
                if (Estado)
                    return LayoutState.None;
                else
                    return LayoutState.Loading;
            }
        }


        public bool HasNoInternetConnection { get; set; }

        public BaseViewModel()
        {

        }

        private void ConnectivityChanged(object sender, ConnectivityChangedEventArgs e)
        {
            HasNoInternetConnection = !e.NetworkAccess.Equals(NetworkAccess.Internet);
        }


        protected virtual void OnPropertyChanged([CallerMemberName] string propertyname = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyname));
        }

        protected bool SetProperty<T>(ref T field, T newValue, [CallerMemberName] string propertyName = null)
        {
            if (!Equals(field, newValue))
            {
                field = newValue;
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
                return true;
            }

            return false;
        }
    }
}
