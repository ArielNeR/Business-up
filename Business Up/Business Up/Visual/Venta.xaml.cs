using Business_Up.Contenido;
using Business_Up.Controls;
using Business_Up.Data;
using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.CommunityToolkit.Extensions;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Venta : ContentPage
    {
        private Cliente Cliente;
        public static List<Carrito> Productos = new List<Carrito>();
        private bool Pagado = true;

        public Venta()
        {
            InitializeComponent();
            botonAtras.Text = "\u2190";
            Actualizar();
        }

        private void Actualizar()
        {
            ViewCarrito.BindingContext = new ContenidoCarrito();
        }

        public void Datos(object dato)
        {
            if(dato is Cliente)
            {
                Cliente cliente = (Cliente)dato;
                Cliente = cliente;
                ElegirCliente.Text = cliente.Nombre;
            }
            else
            {
                Carrito i = (Carrito)dato;
                Agregar(i.Item, i.Cantidad);
            }
            
        }

        public static int verificar(string dato)
        {
            if (dato != null)
            {
                string valor = "";
                char[] cadena = dato.ToCharArray();
                for (int i = 0; i < cadena.Length; i++)
                {
                    if (cadena[i].Equals('.') || cadena[i].Equals(','))
                    {

                    }
                    else
                        valor += cadena[i];
                }
                return Convert.ToInt32(valor);
            }
            else
                return 0;

        }

        private void Agregar(Entidades.Inventario invent, int cantidad)
        {
            bool ingresado = false;
            for (int i = 0; i < Productos.Count; i++)
            {
                if(Productos[i].Item.Nombre.Equals(invent.Nombre))
                {
                    Productos[i].Cantidad += cantidad;
                    ingresado = true;
                    break;
                }
            }
            if (!ingresado)
                Productos.Add(new Carrito(invent, cantidad));
            Actualizar();
            Total.Text = Productos.Sum(x => x.Item.Precio * x.Cantidad).ToString();
        }

        public void Tipo(string tipo)
        {
            TipoPago.Text = tipo;
        }

        private void ElegirCliente_Clicked(object sender, EventArgs e)
        {
            var modalPage = new Clientes(this);
            Application.Current.MainPage.Navigation.PushModalAsync(modalPage);
        }

        private void DeudaPendiente_Toggled(object sender, ToggledEventArgs e)
        {
            Pagado = !e.Value;
            if (e.Value)
            {
                DependencyService.Get<IMensaje>().ShowMessage("Se guardará en deudas por cobrar", 0);
            }
        }

        private async void TipoPago_Clicked(object sender, EventArgs e)
        {
            _ = await Shell.Current.Navigation.ShowPopupAsync(new TipoPago(this) { Size = new Size(200, 300) });
        }

        private void AgregarItem_Clicked(object sender, EventArgs e)
        {
            var modalPage = new Inventario(this);
            Application.Current.MainPage.Navigation.PushModalAsync(modalPage);
            modalPage.Disappearing += (sender2, e2) =>
            {
                Actualizar();
            };
        }

        private async void Facturar_Clicked(object sender, EventArgs e)
        {
            if (Productos.Count > 0 && Cliente != null)
            {
                var result = await DisplayAlert("Generación de factura", "¿Esta segur@ de facturar con los datos actuales?\nComprobar productos y cliente", "Generar factura", "Cancelar");
                if (result)
                {
                    var DatosVenta = new Entidades.Venta(TListaVentas.ListaVentas.Count, Cliente, Productos, Pagado, datePicker.Date, TipoPago.Text, new byte[0]);
                    var factura = DependencyService.Get<PdfFactura>().GenerarPDF(DatosVenta, UsuarioData.UsuarioActual);
                    DatosVenta.Factura = factura;
                    TListaVentas.Ingresar(DatosVenta);
                    foreach (var item in Productos)
                    {
                        if (item.Item.Tipo.Equals("Producto"))
                        {
                            ((Producto)TlistaInventario.BuscarItem(item.Item.Nombre)).CantidadTotal -= item.Cantidad;
                            TlistaInventario.GetItem(item.Item.Nombre).Cantidad -= item.Cantidad;
                        }
                    }
                    TData.GuardarDatosLocal();
                    _ = Task.Run(TData.GuardarDatos);
                    await Application.Current.MainPage.Navigation.PopModalAsync();
                    DependencyService.Get<PdfFactura>().CompartirFactura(factura);
                    Productos.Clear();
                    Cliente = null;
                }
            }
            else
                DependencyService.Get<IMensaje>().ShowMessage("Verifique toda la informacion y rellene todos lo campos requeridos", 0);
        }

        protected override bool OnBackButtonPressed()
        {
            return ComprobacionPerdidaDeDatos();
        }

        private bool ComprobacionPerdidaDeDatos()
        {
            if (Productos.Count > 0 || !(Cliente is null))
            {
                Device.BeginInvokeOnMainThread(async () =>
                {
                    var result = await DisplayAlert("Perdida de datos", "¿Esta segur@ de salir?\nSe perderan todos los datos", "Si, deseo salir", "Cancelar");
                    if (result)
                    {
                        _ = Application.Current.MainPage.Navigation.PopModalAsync();
                        Productos.Clear();
                        Cliente = null;
                    }
                });
            }
            else
                Application.Current.MainPage.Navigation.PopModalAsync();
            return true;
        }

        private async void EliminarItem_Clicked(object sender, EventArgs e)
        {
            var editBoxButton = sender as Button;
            var result = await DisplayAlert("Quitar item de facturacion", "¿Estas segur@?", "Si, estoy segur@", "Cancelar");
            if (editBoxButton != null && result)
            {
                var boxID = editBoxButton.CommandParameter;
                for (int i = 0; i < Productos.Count; i++)
                {
                    if(Productos[i].Item.Nombre == boxID.ToString())
                    {
                        Productos.Remove(Productos[i]);
                        break;
                    }
                }
                Actualizar();
                Total.Text = Productos.Sum(x => x.Item.Precio * x.Cantidad).ToString();
            }
        }

        private async void ModificarCantidadItem_Clicked(object sender, EventArgs e)
        {
            var editBoxButton = sender as Button;
            var boxID = editBoxButton.CommandParameter;
            var result = await DisplayPromptAsync($"Modificar cantidad o veces de item", $"Ingrese cantidad de {boxID}", "Aceptar", "Cancelar", "Ingrese cantidad", 5, Keyboard.Numeric, "");
            try
            {
                int cant = verificar(result);
                if (cant > 0)
                {
                    for (int i = 0; i < Productos.Count; i++)
                    {
                        if (Productos[i].Item.Nombre == boxID.ToString())
                        {
                            if(Productos[i].Item.Tipo.Equals("Producto"))
                            {
                                if(cant <= Productos[i].Item.Cantidad)
                                {
                                    Productos[i].Cantidad = cant;
                                    break;
                                }
                                else
                                {
                                    DependencyService.Get<IMensaje>().ShowMessage("La cantidad ingresada sobrepasa el número de unidades disponibles", 0);
                                }
                            }
                            else
                            {
                                Productos[i].Cantidad = cant;
                                break;
                            }

                        }
                    }
                    Actualizar();
                    Total.Text = Productos.Sum(x => x.Item.Precio * x.Cantidad).ToString();
                }
            }
            catch
            {
                
            }
        }

        private void botonAtras_Clicked(object sender, EventArgs e)
        {
            ComprobacionPerdidaDeDatos();
        }
    }

    [Serializable]
    public class Carrito
    {
        private Entidades.Inventario item;
        private int cantidad;

        public Carrito(Entidades.Inventario item, int cantidad)
        {
            this.item = item;
            this.cantidad = cantidad;
        }

        public Entidades.Inventario Item { get => item; set => item = value; }
        public int Cantidad { get => cantidad; set => cantidad = value; }
        public string DatoCantidad { get { if (item.Tipo.Equals("Producto")) { return "Unidades: " + cantidad; } else { return "Veces realizadas: " + cantidad; } } }

    }

}