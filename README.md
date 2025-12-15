# 💼 Business Up  
Gestión completa de negocios en tu bolsillo: inventario, ventas, clientes, facturación y publicaciones.  
Desarrollado en **Xamarin.Forms 5** (C#) con backend Box Cloud y PDF local.

---

## 📱 ¿Qué puedo hacer?
| Módulo | Funciones |
|---|---|
| **Inventario** | Alta de productos/servicios, stock, precios, código de barras |
| **Ventas** | Carrito rápido, cobro (efectivo/tarjeta/transf.), deuda pendiente |
| **Clientes** | Ficha completa, múltiples contactos y cuentas bancarias |
| **Facturas** | Generación PDF (iTextSharp) → compartir o abrir |
| **Publicaciones** | Crear posts con imagen y descripción; feed interno |
| **Balance** | Gráficos diarios/semanales/mensuales + cuentas por cobrar |
| **Admin** | Suspender/desbloquear usuarios (solo admin) |

---

## 🚀 Demo rápida
1. Descarga el **APK** en [Releases](https://github.com/tu-usuario/Business-Up/releases)  
2. Regístrate o usa `demo@demo.com / demo123`  
3. Crea un producto → vende → genera factura → comparte PDF

---

## 🛠️ Stack
- **Xamarin.Forms 5** – UI cross-platform  
- **Xamarin.Essentials** – cámara, archivos, share  
- **iTextSharp** – PDF nativo  
- **Box API** – almacenamiento cloud (archivos `.save`)  
- **Microcharts** – gráficos de ventas y ganancias  

---

## 📂 Estructura
```
Business-Up/
├── Business Up/                    # PCL – lógica & UI
│   ├── Visual/                     # Vistas XAML
│   ├── Entidades/                  # Modelos Cliente, Venta, Producto…
│   ├── Data/                       # Acceso Box + serialización binaria
│   └── Contenido/                  # ViewModels
├── Business Up.Android/            # Proyecto Android
│   ├── Pdf/                        # Renderer PDF con RecyclerView
│   └── MainActivity.cs             # Entry point
└── Business Up.sln
```

---

## ▶️ Compilar
**Requisitos:**  
- Visual Studio 2022  
- Xamarin + Android SDK 12+  
- Cuenta Box (token CCG en `TDataBox`)

Pasos:
```bash
git clone https://github.com/tu-usuario/Business-Up.git
cd "Business-Up"
# Abre Business Up.sln en VS
# Establece Android como startup → F5
```

---

## 🔐 Permisos Android
- `INTERNET` – Box API  
- `READ/WRITE_EXTERNAL_STORAGE` – PDF y picker de imágenes  

---

## 📄 Licencia
MIT – usa, modifica y distribuye sin restricciones.
```