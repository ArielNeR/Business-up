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
    public class ContenidoClientes : INotifyPropertyChanged
    {
        private ObservableCollection<Cliente> coleccion;
        private bool estado = false;
        public static bool inicio = false;

        public ContenidoClientes(string busqueda)
        {
            MostrarClientes(busqueda);
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

        public ObservableCollection<Cliente> GetClientes
        {
            get
            {
                if (coleccion == null)
                    coleccion = new ObservableCollection<Cliente>();
                return coleccion;
            }
            set
            {
                coleccion = value;
                OnPropertyChanged("Getcoleccion");
            }
        }

        public bool Estado { get => estado; set => estado = value; }

        public void MostrarClientes(string busqueda)
        {
            if(busqueda != string.Empty && busqueda != null)
            {
                List<Cliente> lista = TlistaClientes.ListaClientes;
                coleccion = new ObservableCollection<Cliente>();
                for (int i = 0; i < lista.Count; i++)
                {
                    if (lista[i].Nombre.ToLower().Contains(busqueda.ToLower()) || lista[i].IdCliente.ToLower().Contains(busqueda.ToLower()))
                    {
                        coleccion.Add(lista[i]);
                    }
                }
            }
            else
            {
                if (inicio)
                {
                    List<Cliente> lista = TlistaClientes.ListaClientes;
                    int cont = 0;
                    coleccion = new ObservableCollection<Cliente>();
                    foreach (var item in lista)
                    {
                        if (cont < 5)
                        {
                            coleccion.Add(item);
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
                    List<Cliente> lista = TlistaClientes.ListaClientes;
                    coleccion = new ObservableCollection<Cliente>();
                    for (int i = 0; i < lista.Count; i++)
                    {
                        coleccion.Add(lista[i]);
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
