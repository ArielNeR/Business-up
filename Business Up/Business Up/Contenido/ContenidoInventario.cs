using Business_Up.Data;
using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Text;
using Xamarin.CommunityToolkit.UI.Views;

namespace Business_Up.Contenido
{
    public class ContenidoInventario : INotifyPropertyChanged
    {
        private ObservableCollection<Inventario> coleccionProductos;

        private bool estado = false;
        public static bool inicio = false;

        public ContenidoInventario(string busqueda)
        {
            MostrarInventario(busqueda);
        }

        public LayoutState GetState
        {
            get
            {
                if (estado)
                    return LayoutState.None;
                else
                    return LayoutState.Loading;
            }
        }

        public ObservableCollection<Inventario> GetProductos
        {
            get
            {
                if (coleccionProductos == null)
                    coleccionProductos = new ObservableCollection<Inventario>();
                return coleccionProductos;
            }
            set
            {
                coleccionProductos = value;
                OnPropertyChanged("Getcoleccion");
            }
        }

        public bool Estado { get => estado; set => estado = value; }

        public void MostrarInventario(string busqueda)
        {

            if (busqueda != string.Empty && busqueda != null)
            {
                List<Inventario> lista = TlistaInventario.Inventario;
                coleccionProductos = new ObservableCollection<Inventario>();
                for (int i = 0; i < lista.Count; i++)
                {
                    if (lista[i].Nombre.ToLower().Contains(busqueda.ToLower()) || lista[i].CodigoBarra.ToLower().Contains(busqueda.ToLower()))
                    {
                        coleccionProductos.Add(lista[i]);
                    }
                }
            }
            else
            {
                if (inicio)
                {
                    List<Inventario> lista = TlistaInventario.Inventario;
                    int cont = 0;
                    coleccionProductos = new ObservableCollection<Inventario>();
                    foreach (var item in lista)
                    {
                        if(cont < 5)
                        {
                            coleccionProductos.Add(item);
                            cont++;
                        }
                        else
                        {
                            inicio = false;
                            return;
                        }
                    }
                }
                else
                {
                    List<Inventario> lista = TlistaInventario.Inventario;
                    coleccionProductos = new ObservableCollection<Inventario>();
                    for (int i = 0; i < lista.Count; i++)
                    {
                        coleccionProductos.Add(lista[i]);
                    }
                }
                
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
