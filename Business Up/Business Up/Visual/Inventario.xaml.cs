using Business_Up.Contenido;
using Business_Up.Controls;
using Business_Up.Data;
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
    public partial class Inventario : ContentPage
    {
        private readonly Venta Venta;
        private Entidades.Inventario item;
        public Inventario(Venta venta)
        {
            InitializeComponent();
            Venta = venta;
            if(venta is null)
            {
                PanelSeleccionItem.IsVisible = false;
            }
            else
            {
                PanelSeleccionItem.IsVisible = true;
            }
            ViewProductos.BindingContext = new ContenidoInventario("");
            ViewProductos.RefreshCommand = new Command(() => {
                Actualizar();
                ViewProductos.IsRefreshing = false;
            });
        }

        private void Actualizar()
        {
            ViewProductos.BindingContext = new ContenidoInventario(buscartxt.Text);
        }

        private void ViewProductos_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            if (!(Venta is null))
            {
                //var datos = (Entidades.Inventario)e.Item;
                //Application.Current.MainPage.Navigation.PopModalAsync();
                //Venta.Datos(new Carrito(TlistaInventario.BuscarItem(datos.Nombre), 10));
                item = (Entidades.Inventario)e.Item;
                DatosItem.Text = item.Nombre;
                if (!item.Tipo.Equals("Producto"))
                {
                    labelCantidad.FontSize = 15;
                    labelCantidad.Text = "Veces realizadas";
                }
                else
                {
                    labelCantidad.FontSize = 20;
                    labelCantidad.Text = "Cantidad";
                }
            }
            else
            {

            }
        }

        private void Animate()
        {
            if (!Picker.IsVisible)
            {
                Picker.IsVisible = !Picker.IsVisible;
                Picker.AnchorX = 1;
                Picker.AnchorY = 1;

                Animation scaleAnimation = new Animation(
                    f => Picker.Scale = f,
                    0.5,
                    1,
                    Easing.Linear);

                Animation fadeAnimation = new Animation(
                    f => Picker.Opacity = f,
                    0.2,
                    1,
                    Easing.Linear);

                scaleAnimation.Commit(Picker, "popupScaleAnimation", 10);
                fadeAnimation.Commit(Picker, "popupFadeAnimation", 10);
            }
            else
            {
                /*await Task.WhenAny<bool>
                  (
                    Picker.FadeTo(0, 500, Easing.SinInOut)
                  );*/

                Picker.IsVisible = !Picker.IsVisible;
            }
        }

        private void Limpiar()
        {
            DatosItem.Text = "Item";
            cantidadtxt.Text = "0";
            item = null;
        }

        private bool MenorAStock()
        {
            if(Venta.Productos.Count > 0)
            {
                for (int i = 0; i < Venta.Productos.Count; i++)
                {
                    if(Venta.Productos[i].Item.Nombre == item.Nombre)
                    {
                        if (Venta.Productos[i].Cantidad + Convert.ToInt32(cantidadtxt.Text) > item.Cantidad)
                        {
                            return false;
                        }
                    }
                    
                }
                if (item.Cantidad < Convert.ToInt32(cantidadtxt.Text))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else
            {
                if(item.Cantidad < Convert.ToInt32(cantidadtxt.Text))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            
        }

        private void AgregarProducto_Clicked(object sender, EventArgs e)
        {
            Animate();
            var modalPage = new AgregarProducto(null);
            Application.Current.MainPage.Navigation.PushModalAsync(modalPage);
            modalPage.Disappearing += (sender2, e2) =>
            {
                Actualizar();
            };
        }

        private void AgregarServicio_Clicked(object sender, EventArgs e)
        {
            Animate();
            var modalPage = new AgregarServicio(null);
            Application.Current.MainPage.Navigation.PushModalAsync(modalPage);
            modalPage.Disappearing += (sender2, e2) =>
            {
                Actualizar();
            };
        }

        private void buttonAgregar_Clicked(object sender, EventArgs e)
        {
            Animate();
        }

        private void buscartxt_TextChanged(object sender, TextChangedEventArgs e)
        {
            Actualizar();
        }

        private void Atras_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PopModalAsync();
        }

        private void Aceptar_Clicked(object sender, EventArgs e)
        {
            if(cantidadtxt.Text != string.Empty && !(cantidadtxt.Text is null) && cantidadtxt.Text != "0" && DatosItem.Text != "Item")
            {
                if(item.Tipo.Equals("Producto"))
                {
                    if (MenorAStock())
                    {
                        Venta.Datos(new Carrito(item, Convert.ToInt32(cantidadtxt.Text)));
                        DependencyService.Get<IMensaje>().ShowMessage("Item agregado", 0);
                        Limpiar();
                    }
                    else
                        DependencyService.Get<IMensaje>().ShowMessage("Cantidad de unidades no disponibles", 0);
                }
                else
                {
                    Venta.Datos(new Carrito(item, Convert.ToInt32(cantidadtxt.Text)));
                    DependencyService.Get<IMensaje>().ShowMessage("Item agregado", 0);
                    Limpiar();
                }
                
            }
            else
                DependencyService.Get<IMensaje>().ShowMessage("Verifique la cantidad y/o seleccione un item", 0);

        }

        private void cantidadtxt_Completed(object sender, EventArgs e)
        {
            if(cantidadtxt.Text is null || cantidadtxt.Text == string.Empty)
            {
                cantidadtxt.Text = "0";
            }
        }

        private void cantidadtxt_Unfocused(object sender, FocusEventArgs e)
        {
            if(cantidadtxt.Text is null || cantidadtxt.Text == string.Empty)
            {
                cantidadtxt.Text = "0";
            }
        }

        private void cantidadtxt_Focused(object sender, FocusEventArgs e)
        {
            if (cantidadtxt.Text == "0")
            {
                cantidadtxt.Text = "";
            }
        }

        private async void EliminarItem_Clicked(object sender, EventArgs e)
        {
            var editBoxButton = sender as Button;
            var result = await DisplayAlert("Quitar item del inventario", "¿Estas segur@?", "Si, estoy segur@", "Cancelar");
            if (editBoxButton != null && result)
            {
                var boxID = editBoxButton.CommandParameter as Entidades.Inventario;
                for (int i = 0; i < TlistaInventario.Inventario.Count; i++)
                {
                    if (TlistaInventario.Inventario[i].Nombre == boxID.Nombre)
                    {
                        TlistaInventario.Inventario.Remove(TlistaInventario.Inventario[i]);
                        break;
                    }
                }
                Actualizar();
                _ = Task.Run(TData.GuardarDatos);
            }
        }

        private async void ModificarCantidadItem_Clicked(object sender, EventArgs e)
        {
            var editBoxButton = sender as Button;
            var boxID = editBoxButton.CommandParameter as Entidades.Inventario;
            if (boxID.Tipo.Equals("Producto"))
            {
                await Application.Current.MainPage.Navigation.PushModalAsync(new AgregarProducto(boxID));
            }
            else
            {
                await Application.Current.MainPage.Navigation.PushModalAsync(new AgregarServicio(boxID));
            }
        }
    }
}