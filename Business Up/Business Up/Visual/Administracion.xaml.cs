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
    public partial class Administracion : TabbedPage
    {
        Balance balance = new Balance();
        Inventario inventario = new Inventario(null);
        Clientes clientes = new Clientes(null);
        public Administracion()
        {
            InitializeComponent();
            Children.Add(balance);
            Children.Add(inventario);
            Children.Add(clientes);

        }
    }
}