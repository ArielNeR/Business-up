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
    public partial class TipoBalance
    {
        private readonly Balance ba;
        public TipoBalance(Balance b)
        {
            InitializeComponent();
            ba = b;
            
        }

        private void Diario_Clicked(object sender, EventArgs e)
        {
            ba.Tipo("Diario");
            Dismiss(this);
        }

        private void Semanal_Clicked(object sender, EventArgs e)
        {
            ba.Tipo("Semanal");
            Dismiss(this);
        }

        private void Mensual_Clicked(object sender, EventArgs e)
        {
            ba.Tipo("Mensual");
            Dismiss(this);
        }

        private void Anual_Clicked(object sender, EventArgs e)
        {
            ba.Tipo("Anual");
            Dismiss(this);
        }
    }
}