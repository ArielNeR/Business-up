using Android.Views;
using Android.Widget;
using AndroidX.RecyclerView.Widget;
using Business_Up.Droid;

namespace PruebaPDF.Droid.Pdf
{
    public class CardViewHolder : RecyclerView.ViewHolder
    {
        public ImageView Image { get; private set; }

        public CardViewHolder(View itemView) : base(itemView)
        {
            Image = itemView.FindViewById<ImageView>(Resource.Id.imageView);
        }
    }
}