using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;

namespace TypeRushTools
{
    class IconGenerator
    {
        static void Main(string[] args)
        {
            string baseDir = AppDomain.CurrentDomain.BaseDirectory;
            string projectDir = Directory.GetParent(baseDir).FullName;
            string assetsDir = Path.Combine(projectDir, "assets");
            if (!Directory.Exists(assetsDir))
            {
                Directory.CreateDirectory(assetsDir);
            }

            // 1. Generate 512x512 Master PNG
            using (Bitmap bmp512 = CreateAppBitmap(512))
            {
                string pngMasterPath = Path.Combine(assetsDir, "logo-master.png");
                bmp512.Save(pngMasterPath, ImageFormat.Png);
            }

            // 2. Generate 256x256 App PNG (used in Java Swing window icon)
            string pngPath = Path.Combine(assetsDir, "icon.png");
            using (Bitmap bmp256 = CreateAppBitmap(256))
            {
                bmp256.Save(pngPath, ImageFormat.Png);
                Console.WriteLine("[OK] Generated PNG: " + pngPath);
            }

            // 3. Generate multi-resolution Windows .ICO (256, 128, 64, 48, 32, 16)
            int[] sizes = new int[] { 256, 128, 64, 48, 32, 16 };
            byte[][] pngBytes = new byte[sizes.Length][];

            for (int i = 0; i < sizes.Length; i++)
            {
                using (Bitmap bmp = CreateAppBitmap(sizes[i]))
                using (MemoryStream ms = new MemoryStream())
                {
                    bmp.Save(ms, ImageFormat.Png);
                    pngBytes[i] = ms.ToArray();
                }
            }

            string icoPath = Path.Combine(assetsDir, "icon.ico");
            using (FileStream fs = File.Create(icoPath))
            using (BinaryWriter bw = new BinaryWriter(fs))
            {
                // ICONHEADER (6 bytes)
                bw.Write((ushort)0); // Reserved
                bw.Write((ushort)1); // Resource type (1 = icon)
                bw.Write((ushort)sizes.Length); // Image count

                int offset = 6 + (16 * sizes.Length);

                // ICONDIRENTRY (16 bytes per image)
                for (int i = 0; i < sizes.Length; i++)
                {
                    int sz = sizes[i];
                    bw.Write((byte)(sz >= 256 ? 0 : sz)); // Width
                    bw.Write((byte)(sz >= 256 ? 0 : sz)); // Height
                    bw.Write((byte)0); // Color count
                    bw.Write((byte)0); // Reserved
                    bw.Write((ushort)1); // Color planes
                    bw.Write((ushort)32); // Bits per pixel
                    bw.Write((uint)pngBytes[i].Length); // Image bytes length
                    bw.Write((uint)offset); // File offset
                    offset += pngBytes[i].Length;
                }

                // Write PNG image bytes
                for (int i = 0; i < sizes.Length; i++)
                {
                    bw.Write(pngBytes[i]);
                }
            }

            Console.WriteLine("[OK] Generated Multi-Res Windows ICO: " + icoPath);
        }

        static Bitmap CreateAppBitmap(int size)
        {
            Bitmap bmp = new Bitmap(size, size, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(bmp))
            {
                g.SmoothingMode = SmoothingMode.AntiAlias;
                g.InterpolationMode = InterpolationMode.HighQualityBicubic;
                g.PixelOffsetMode = PixelOffsetMode.HighQuality;
                g.Clear(Color.Transparent);

                float scale = size / 256.0f;

                // 1. Sleek rounded dark rectangle surface
                float pad = 10.0f * scale;
                float w = size - (pad * 2);
                float h = size - (pad * 2);
                float radius = 52.0f * scale;

                using (GraphicsPath cardPath = CreateRoundedRect(pad, pad, w, h, radius))
                {
                    // Gradient Fill (Dark Matte Charcoal)
                    using (LinearGradientBrush bgBrush = new LinearGradientBrush(
                        new PointF(0, pad),
                        new PointF(0, pad + h),
                        Color.FromArgb(255, 34, 34, 40),
                        Color.FromArgb(255, 18, 18, 22)))
                    {
                        g.FillPath(bgBrush, cardPath);
                    }

                    // Outer border with subtle golden glow
                    using (Pen borderPen = new Pen(Color.FromArgb(255, 226, 183, 20), 4.5f * scale))
                    {
                        g.DrawPath(borderPen, cardPath);
                    }
                }

                // 2. Center warm ambient light behind the lightning bolt
                using (GraphicsPath glowPath = new GraphicsPath())
                {
                    glowPath.AddEllipse(40.0f * scale, 40.0f * scale, 176.0f * scale, 176.0f * scale);
                    using (PathGradientBrush pgb = new PathGradientBrush(glowPath))
                    {
                        pgb.CenterColor = Color.FromArgb(70, 245, 158, 11);
                        pgb.SurroundColors = new Color[] { Color.FromArgb(0, 245, 158, 11) };
                        g.FillPath(pgb, glowPath);
                    }
                }

                // 3. Lightning Bolt (Speed Symbol)
                PointF[] boltPoints = new PointF[]
                {
                    new PointF(146.0f * scale, 36.0f * scale),
                    new PointF(86.0f * scale, 134.0f * scale),
                    new PointF(134.0f * scale, 134.0f * scale),
                    new PointF(110.0f * scale, 224.0f * scale),
                    new PointF(182.0f * scale, 116.0f * scale),
                    new PointF(134.0f * scale, 116.0f * scale)
                };

                using (GraphicsPath boltPath = new GraphicsPath())
                {
                    boltPath.AddPolygon(boltPoints);

                    // Golden Amber Gradient Fill
                    using (LinearGradientBrush boltBrush = new LinearGradientBrush(
                        new PointF(0, 36.0f * scale),
                        new PointF(0, 224.0f * scale),
                        Color.FromArgb(255, 255, 228, 92),
                        Color.FromArgb(255, 226, 183, 20)))
                    {
                        g.FillPath(boltBrush, boltPath);
                    }

                    // Crisp contour line
                    using (Pen boltPen = new Pen(Color.FromArgb(255, 245, 158, 11), 2.0f * scale))
                    {
                        g.DrawPath(boltPen, boltPath);
                    }
                }

                // 4. Subtle speed dash accents on bottom left
                using (Pen dashPen = new Pen(Color.FromArgb(180, 226, 183, 20), 3.0f * scale))
                {
                    dashPen.StartCap = LineCap.Round;
                    dashPen.EndCap = LineCap.Round;
                    g.DrawLine(dashPen, 46.0f * scale, 178.0f * scale, 68.0f * scale, 178.0f * scale);
                    g.DrawLine(dashPen, 52.0f * scale, 194.0f * scale, 82.0f * scale, 194.0f * scale);
                }
            }

            return bmp;
        }

        static GraphicsPath CreateRoundedRect(float x, float y, float w, float h, float r)
        {
            GraphicsPath path = new GraphicsPath();
            float d = r * 2.0f;
            path.AddArc(x, y, d, d, 180, 90);
            path.AddArc(x + w - d, y, d, d, 270, 90);
            path.AddArc(x + w - d, y + h - d, d, d, 0, 90);
            path.AddArc(x, y + h - d, d, d, 90, 90);
            path.CloseFigure();
            return path;
        }
    }
}
