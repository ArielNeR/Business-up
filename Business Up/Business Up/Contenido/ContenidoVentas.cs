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
    public class ContenidoVentas : INotifyPropertyChanged
    {
        private ObservableCollection<Venta> coleccion;
        private bool estado = false;

        public ContenidoVentas(string busqueda, int tipoVista)
        {
            MostrarVentas(busqueda, tipoVista);
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

        public ObservableCollection<Venta> GetVentas
        {
            get
            {
                if (coleccion == null)
                    coleccion = new ObservableCollection<Venta>();
                return coleccion;
            }
            set
            {
                coleccion = value;
                OnPropertyChanged("Getcoleccion");
            }
        }

        public bool Estado { get => estado; set => estado = value; }

        public void MostrarVentas(string busqueda, int tipoVista)
        {
            try
            {

                if (tipoVista == 0)
                {
                    List<Venta> lista = TListaVentas.ListaVentas;
                    lista.Sort((a, b) => b.Total.CompareTo(a.Total));
                    coleccion = new ObservableCollection<Venta>();
                    int cont = 0;
                    foreach (var item in lista)
                    {
                        if (cont < 5)
                        {
                            if (!item.Pagado)
                            {
                                coleccion.Add(item);
                                cont++;
                            }
                        }
                        else
                        {
                            return;
                        }
                    }
                }
                else
                {
                    if(tipoVista == 1)
                    {
                        List<Venta> lista = TListaVentas.ListaVentas;
                        coleccion = new ObservableCollection<Venta>();
                        foreach (var item in lista)
                        {
                            if (!item.Pagado)
                            {
                                coleccion.Add(item);
                            }
                        }
                    }
                    else
                    {

                        if (busqueda != string.Empty && busqueda != null)
                        {
                            List<Venta> lista = TListaVentas.ListaVentas;
                            coleccion = new ObservableCollection<Venta>();
                            for (int i = 0; i < lista.Count; i++)
                            {
                                if (lista[i].Fecha.ToString("d").ToLower().Contains(busqueda.ToLower()) || lista[i].Cliente.Nombre.ToLower().Contains(busqueda.ToLower()))
                                {
                                    coleccion.Add(lista[i]);
                                }
                            }
                        }
                        else
                        {
                            List<Venta> lista = TListaVentas.ListaVentas;
                            coleccion = new ObservableCollection<Venta>();
                            foreach (var item in lista)
                            {
                                if (item.Pagado)
                                {
                                    coleccion.Add(item);
                                }
                            }
                        }

                    }
                    
                }

            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return;
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
