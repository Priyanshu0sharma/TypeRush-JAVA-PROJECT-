Add-Type -AssemblyName System.Drawing

$assetsDir = Join-Path $PSScriptRoot "..\assets"
if (-not (Test-Path $assetsDir)) {
    New-Item -ItemType Directory -Path $assetsDir -Force | Out-Null
}

function Create-AppBitmap([int]$size) {
    $bmp = New-Object System.Drawing.Bitmap $size, $size
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.Clear([System.Drawing.Color]::Transparent)

    $scale = $size / 256.0

    # Background rounded rect
    $bgBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush (
        (New-Object System.Drawing.PointF 0, 0),
        (New-Object System.Drawing.PointF 0, $size),
        ([System.Drawing.Color]::FromArgb(255, 30, 30, 36)),
        ([System.Drawing.Color]::FromArgb(255, 18, 18, 22))
    )

    $radius = 48.0 * $scale
    $rect = New-Object System.Drawing.RectangleF (8.0 * $scale), (8.0 * $scale), (240.0 * $scale), (240.0 * $scale)
    $path = New-Object System.Drawing.Drawing2D.GraphicsPath
    $diameter = $radius * 2.0
    $arc = New-Object System.Drawing.RectangleF $rect.X, $rect.Y, $diameter, $diameter
    $path.AddArc($arc, 180, 90)
    $arc.X = $rect.Right - $diameter
    $path.AddArc($arc, 270, 90)
    $arc.Y = $rect.Bottom - $diameter
    $path.AddArc($arc, 0, 90)
    $arc.X = $rect.Left
    $path.AddArc($arc, 90, 90)
    $path.CloseFigure()

    $g.FillPath($bgBrush, $path)

    # Border Glow
    $borderPen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(255, 226, 183, 20)), (6.0 * $scale)
    $g.DrawPath($borderPen, $path)

    # Inner circular soft glow
    $glowBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush (
        (New-Object System.Drawing.PointF ($size/2), (40*$scale)),
        (New-Object System.Drawing.PointF ($size/2), (220*$scale)),
        ([System.Drawing.Color]::FromArgb(40, 245, 158, 11)),
        ([System.Drawing.Color]::FromArgb(0, 245, 158, 11))
    )
    $g.FillEllipse($glowBrush, (30.0*$scale), (30.0*$scale), (196.0*$scale), (196.0*$scale))

    # Lightning Bolt Symbol (Signature TypeRush / Monkeytype Warm Amber)
    $boltBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush (
        (New-Object System.Drawing.PointF 0, (28*$scale)),
        (New-Object System.Drawing.PointF 0, (228*$scale)),
        ([System.Drawing.Color]::FromArgb(255, 255, 215, 64)),
        ([System.Drawing.Color]::FromArgb(255, 226, 183, 20))
    )

    $boltPath = New-Object System.Drawing.Drawing2D.GraphicsPath
    $pts = @(
        New-Object System.Drawing.PointF (146.0 * $scale), (34.0 * $scale),
        New-Object System.Drawing.PointF (86.0 * $scale), (138.0 * $scale),
        New-Object System.Drawing.PointF (136.0 * $scale), (138.0 * $scale),
        New-Object System.Drawing.PointF (112.0 * $scale), (226.0 * $scale),
        New-Object System.Drawing.PointF (180.0 * $scale), (116.0 * $scale),
        New-Object System.Drawing.PointF (132.0 * $scale), (116.0 * $scale)
    )
    $boltPath.AddPolygon($pts)
    $g.FillPath($boltBrush, $boltPath)

    # Subtle inner shadow on bolt
    $boltPen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(220, 245, 158, 11)), (1.5 * $scale)
    $g.DrawPath($boltPen, $boltPath)

    $g.Dispose()
    return $bmp
}

# 1. Create PNG icons
$pngPath = Join-Path $assetsDir "icon.png"
$bmp256 = Create-AppBitmap 256
$bmp256.Save($pngPath, [System.Drawing.Imaging.ImageFormat]::Png)
Write-Host "[SUCCESS] Created PNG logo at $pngPath"

# 2. Create proper multi-size Windows .ICO file
$icoPath = Join-Path $assetsDir "icon.ico"
$sizes = @(256, 128, 64, 48, 32, 16)
$images = @()
$pngStreams = @()

foreach ($s in $sizes) {
    $bmp = Create-AppBitmap $s
    $ms = New-Object System.IO.MemoryStream
    $bmp.Save($ms, [System.Drawing.Imaging.ImageFormat]::Png)
    $pngStreams += $ms
    $images += @{
        Size = $s
        Data = $ms.ToArray()
    }
}

$fs = [System.IO.File]::Create($icoPath)
$bw = New-Object System.IO.BinaryWriter($fs)

# ICONHEADER
$bw.Write([uint16]0) # Reserved
$bw.Write([uint16]1) # Type (1 = ICO)
$bw.Write([uint16]$images.Count) # Count

$offset = 6 + (16 * $images.Count)

foreach ($img in $images) {
    $width = if ($img.Size -ge 256) { 0 } else { [byte]$img.Size }
    $height = if ($img.Size -ge 256) { 0 } else { [byte]$img.Size }
    $bw.Write([byte]$width)
    $bw.Write([byte]$height)
    $bw.Write([byte]0) # Colors
    $bw.Write([byte]0) # Reserved
    $bw.Write([uint16]1) # Color planes
    $bw.Write([uint16]32) # Bits per pixel
    $bw.Write([uint32]$img.Data.Length) # Size of image data
    $bw.Write([uint32]$offset) # Offset
    $offset += $img.Data.Length
}

foreach ($img in $images) {
    $bw.Write($img.Data)
}

$bw.Flush()
$fs.Close()
Write-Host "[SUCCESS] Created high-resolution Windows ICO at $icoPath"
