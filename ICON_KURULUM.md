# Uygulama İkonu Kurulum Rehberi

## Adım 1: İkon Dosyalarını Hazırlayın

İkon dosyanızı farklı boyutlarda hazırlamanız gerekiyor:

- **mdpi**: 48x48 piksel
- **hdpi**: 72x72 piksel  
- **xhdpi**: 96x96 piksel
- **xxhdpi**: 144x144 piksel
- **xxxhdpi**: 192x192 piksel

## Adım 2: İkon Dosyalarını Klasörlere Ekleyin

İkon dosyalarınızı şu klasörlere kopyalayın:

```
app/src/main/res/
├── mipmap-mdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png (opsiyonel - yuvarlak ikon)
├── mipmap-hdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
├── mipmap-xhdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
├── mipmap-xxhdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
└── mipmap-xxxhdpi/
    ├── ic_launcher.png
    └── ic_launcher_round.png
```

## Adım 3: AndroidManifest'i Güncelleyin

İkon dosyalarını ekledikten sonra `app/src/main/AndroidManifest.xml` dosyasında şu satırları ekleyin:

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    ...>
```

## Hızlı Yöntem: Android Studio Image Asset

1. Android Studio'da `app` klasörüne sağ tıklayın
2. **New > Image Asset** seçin
3. **Launcher Icons (Adaptive and Legacy)** seçin
4. İkon dosyanızı yükleyin veya oluşturun
5. **Next** ve **Finish** butonlarına tıklayın

Bu yöntem otomatik olarak tüm boyutları oluşturur ve doğru klasörlere yerleştirir.

## Not

- İkon dosyaları PNG formatında olmalıdır
- Şeffaf arka plan kullanabilirsiniz
- `ic_launcher_round.png` yuvarlak ikon için kullanılır (Android 7.1+)

