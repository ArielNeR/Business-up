using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Text;
using System.Windows.Input;
using Xamarin.Forms;

namespace Business_Up.Contenido
{
    public class ContenidoCarrito : INotifyPropertyChanged
    {

        private ObservableCollection<Visual.Carrito> coleccion;

        public ContenidoCarrito()
        {
            MostrarCarrito();
        }

        public ObservableCollection<Visual.Carrito> GetProductos
        {
            get
            {
                if (coleccion == null)
                    coleccion = new ObservableCollection<Visual.Carrito>();
                return coleccion;
            }
            set
            {
                coleccion = value;
                OnPropertyChanged("Getcoleccion");
            }
        }

        public void MostrarCarrito()
        {
            List<Visual.Carrito> lista = Visual.Venta.Productos;
            coleccion = new ObservableCollection<Visual.Carrito>();
            for (int i = 0; i < lista.Count; i++)
            {
                coleccion.Add(lista[i]);
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;
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
