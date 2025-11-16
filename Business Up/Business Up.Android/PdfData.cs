using Android.Content;
using Business_Up.Data;
using Business_Up.Droid;
using Business_Up.Entidades;
using iTextSharp.text;
using iTextSharp.text.pdf;
using Java.IO;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;
using Font = iTextSharp.text.Font;
using Image = iTextSharp.text.Image;

[assembly: Dependency(typeof(PdfData))]
namespace Business_Up.Droid
{
    public class PdfData : PdfFactura
    {
        public static byte[] pdfContent;
        public byte[] GenerarPDF(Venta DatosVenta, Usuario DatosUsuario)
        {
            try
            {
                string ruta = System.Environment.GetFolderPath(Environment.SpecialFolder.Personal);
                // Creamos el documento con el tamaño de página y los margenes deseados
                Document document = new Document(PageSize.LETTER, 30, 30, 42, 35);
                MemoryStream file = new MemoryStream();
                //FileStream file = new FileStream(ruta + "/Factura.pdf", FileMode.Create);
                // Creamos una instancia de la clase PdfWriter que nos permite escribir en el documento
                PdfWriter writer = PdfWriter.GetInstance(document, file);
                // Abrimos el documento para comenzar a escribir en él
                document.Open();

                Font font = new Font(Font.HELVETICA, 12, Font.BOLD);
                // Creamos un párrafo y lo añadimos al documento
                Paragraph encabezado = new Paragraph("FACTURA", font);
                encabezado.Alignment = iTextSharp.text.Element.ALIGN_CENTER;
                document.Add(encabezado);

                // Añadir el logo de la empresa
                Image logo = Image.GetInstance(DatosUsuario.FotoPerfil);
                logo.Alignment = iTextSharp.text.Element.ALIGN_RIGHT;
                logo.ScaleToFit(100, 100);
                document.Add(logo);

                Paragraph numero = new Paragraph($"Número de factura: {DatosVenta.NumeroFactura}", font);
                numero.Alignment = iTextSharp.text.Element.ALIGN_LEFT;
                document.Add(numero);

                // Añadir la fecha de emisión
                Paragraph fecha = new Paragraph($"Fecha de emisión: {DatosVenta.Fecha.ToString("d")}", font);
                fecha.Alignment = iTextSharp.text.Element.ALIGN_LEFT;
                document.Add(fecha);

                // Añadir el nombre del cliente
                Paragraph cliente = new Paragraph($"Cliente: {DatosVenta.Cliente.Nombre}\n\n", font);
                cliente.Alignment = iTextSharp.text.Element.ALIGN_LEFT;
                document.Add(cliente);

                // Crear una tabla para los detalles de la factura
                PdfPTable tabla = new PdfPTable(3);
                tabla.WidthPercentage = 100;

                // Añadir las cabeceras de la tabla
                tabla.AddCell(new PdfPCell(new Phrase("Descripción", font)));
                tabla.AddCell(new PdfPCell(new Phrase("Cantidad", font)));
                tabla.AddCell(new PdfPCell(new Phrase("Precio", font)));

                double totalCantidad = 0;
                // Añadir los detalles de la factura
                foreach (var item in DatosVenta.Productos)
                {
                    tabla.AddCell(item.Item.Nombre);
                    tabla.AddCell(item.Cantidad.ToString());
                    tabla.AddCell(item.Item.Precio.ToString());
                    totalCantidad += item.Cantidad * item.Item.Precio;
                }
                // Añadir la tabla al documento
                document.Add(tabla);

                // Añadir el total de la factura
                Paragraph total = new Paragraph($"Total: ${totalCantidad}", font);
                total.Alignment = iTextSharp.text.Element.ALIGN_RIGHT;
                document.Add(total);

                // Cerramos el documento para terminar de escribir en él
                document.Close();
                return file.GetBuffer();
            }
            catch (Exception ex)
            {
                MessageCustom m = new MessageCustom();
                m.ShowMessage("Error al generar el documento factura.\n" + ex.Message, 0);
                return null;
            }
        }

        public void CompartirFactura(byte[] datos)
        {
            MemoryStream memoryStream = new MemoryStream(datos);
            string ruta = System.Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            using (FileStream fileStream = new FileStream(ruta + "/Factura.pdf", FileMode.Create))
            {
                memoryStream.WriteTo(fileStream);
                _ = Share.RequestAsync(new ShareFileRequest(new ShareFile(ruta + "/Factura.pdf")));
            }

        }

        public void AbrirFactura(byte[] datos)
        {
            pdfContent = datos;
        }

        public byte[] GenerarComentario(string comentario)
        {
            try
            {
                string ruta = System.Environment.GetFolderPath(Environment.SpecialFolder.Personal);
                Document document = new Document(PageSize.LETTER, 10, 10, 42, 35);
                MemoryStream stream = new MemoryStream();
                PdfWriter writer = PdfWriter.GetInstance(document, stream);
                document.Open();
                Font font = new Font(Font.HELVETICA, 12, Font.BOLD);
                Paragraph encabezado = new Paragraph("COMENTARIO", font);
                encabezado.Alignment = iTextSharp.text.Element.ALIGN_CENTER;
                document.Add(encabezado);
                Paragraph coment = new Paragraph(comentario, font);
                coment.Alignment = iTextSharp.text.Element.ALIGN_LEFT;
                document.Add(coment);
                document.Close();
                return stream.ToArray();
            }
            catch (Exception ex)
            {
                MessageCustom m = new MessageCustom();
                m.ShowMessage("Error al generar el comentario.\n" + ex.Message, 0);
                return null;
            }
        }

    }
}