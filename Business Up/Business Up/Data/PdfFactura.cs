
namespace Business_Up.Data
{
    public interface PdfFactura
    {
        byte[] GenerarPDF(Entidades.Venta venta, Entidades.Usuario usuario);
        byte[] GenerarComentario(string comentario);
        void CompartirFactura(byte[] datos);
        void AbrirFactura(byte[] datos);
    }
}
