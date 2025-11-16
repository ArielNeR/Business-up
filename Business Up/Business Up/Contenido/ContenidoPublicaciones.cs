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
    public class ContenidoPublicaciones : INotifyPropertyChanged
    {

        private ObservableCollection<Publicacion> coleccion;
        private bool estado = false;

        public ContenidoPublicaciones(bool Mio)
        {
            MostrarPublicaciones(Mio);
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

        public ObservableCollection<Publicacion> GetPublicaciones
        {
            get
            {
                if (coleccion == null)
                    coleccion = new ObservableCollection<Publicacion>();
                return coleccion;
            }
            set
            {
                coleccion = value;
                OnPropertyChanged("Getcoleccion");
            }
        }

        public bool Estado { get => estado; set => estado = value; }

        public void MostrarPublicaciones(bool Mio)
        {
            if (Mio)
            {
                List<Publicacion> lista = TListaPublicaciones.ListaPublicaciones;
                coleccion = new ObservableCollection<Publicacion>();
                for (int i = 0; i < lista.Count; i++)
                {
                    if(lista[i].Usuario.Nombre == UsuarioData.UsuarioActual.Nombre)
                    {
                        coleccion.Add(lista[i]);
                    }
                }
            }
            else
            {
                List<Publicacion> lista = TListaPublicaciones.ListaPublicaciones;
                coleccion = new ObservableCollection<Publicacion>();
                for (int i = 0; i < lista.Count; i++)
                {
                    coleccion.Add(lista[i]);
                    Console.WriteLine("numm"+i);
                }
                if (lista.Count == 0)
                {
                    coleccion.Add(new Publicacion(new Usuario("Bussines Up", "", "", new byte[0]), 100, new byte[0], "Bienvenido usuario", "Publicaciones en matenimiento.."));
                }
            }
        }

        /// <summary>
        /// Evento que se lanza cuando una propiedad de la clase ha cambiado.
        /// </summary>
        public event PropertyChangedEventHandler PropertyChanged;

        /// <summary>
        /// Método que invoca el evento PropertyChanged. Se llama automáticamente cuando una propiedad ha cambiado.
        /// </summary>
        /// <param name="propertyname">El nombre de la propiedad que ha cambiado. Este valor se establece automáticamente a través del atributo CallerMemberName.</param>
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
