using Business_Up.Contenido;
using Business_Up.Controls;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;


using static Business_Up.Data.TData;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Publicaciones : ContentPage
    {
        public Publicaciones()
        {
            InitializeComponent();
            ICommand refreshCommand = new Command(() =>
            {
                ActualizarPublicaciones();
                refreshView.IsRefreshing = false;
            });
            refreshView.Command = refreshCommand;
            ActualizarPublicaciones();
        }

        /**
        Método que actualiza las publicaciones en la aplicación.
        Si no se está actualizando ya, se llama al método para obtener las publicaciones de la API en un hilo de ejecución en segundo plano.
        Después de la obtención de las publicaciones, se actualiza el binding context de la lista de publicaciones con el nuevo contenido.
        @return void
        */
        public void ActualizarPublicaciones()
        {
            if (!Data.TData.actualizando)
            {
                Device.BeginInvokeOnMainThread(async () =>
                {
                    Data.TData.actualizando = true;
                    await Task.Delay(200);
                    var res = await Data.TData.ObtenerPublicaciones();
                    ListaPublicaciones.BindingContext = new ContenidoPublicaciones(false);
                    Data.TData.actualizando = false;
                });
            }
        }

        private void CorreoContacto_Clicked(object sender, EventArgs e)
        {
            DependencyService.Get<OpenApp>().AbrirApp((sender as ImageButton).CommandParameter.ToString());
        }
    }
}
