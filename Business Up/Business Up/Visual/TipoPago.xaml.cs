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
    public partial class TipoPago
    {
        private readonly Venta venta;
        public TipoPago(Venta v)
        {
            InitializeComponent();
            venta = v;
        }

        private void Efectivo_Clicked(object sender, EventArgs e)
        {
            venta.Tipo("Efectivo");
            Dismiss(this);
        }

        private void Tarjeta_Clicked(object sender, EventArgs e)
        {
            venta.Tipo("Tarjeta");
            Dismiss(this);
        }

        private void Transaccion_Clicked(object sender, EventArgs e)
        {
            venta.Tipo("Transaccion");
            Dismiss(this);
        }

        private void Otro_Clicked(object sender, EventArgs e)
        {
            venta.Tipo("Otro");
            Dismiss(this);
        }
    }
}