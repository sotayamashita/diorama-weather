# Diorama Weather - 仕様書

## 概要

Nano Banana Pro スタイルのアイソメトリック・ミニチュア3D都市景観を使用した天気ウィジェットアプリ。ユーザーの現在地の天気をビジュアルリッチに表示する。

| 項目 | 内容 |
|------|------|
| アプリ名 | Diorama Weather |
| リポジトリ | `diorama-weather` |
| プラットフォーム | Android |
| 形式 | ホーム画面ウィジェット (4x2) |
| 画像方式 | ハイブリッド（プリレンダリング + テキストオーバーレイ） |

---

## 機能要件

### ウィジェット表示

```
┌────────────────────────────────┐
│         [都市名]               │  ← Roboto Bold, 動的カラー
│           🌤️                   │  ← 天気アイコン
│        12月4日                 │  ← Roboto Regular, 小
│          18°                   │  ← Roboto Medium, 中
│                                │
│   ┌──────────────────────┐    │
│   │                      │    │
│   │   [AI生成画像]        │    │
│   │   汎用都市×天気×時間帯  │    │
│   │                      │    │
│   └──────────────────────┘    │
└────────────────────────────────┘
```

### 表示要素

| 要素 | フォント | サイズ | 配置 |
|------|----------|--------|------|
| 都市名 | Roboto Bold | 大 | 上部中央 |
| 天気アイコン | システム絵文字 | 中 | 都市名下 |
| 日付 | Roboto Regular | 小 | アイコン下 |
| 気温 | Roboto Medium | 中 | 日付下 |

### テキストカラー（動的）

| 条件 | テキスト色 | シャドウ |
|------|------------|----------|
| 朝 / 晴れ系 | `#333333` | 白シャドウ |
| 昼 / 晴れ系 | `#FFFFFF` | 黒シャドウ |
| 夕方 | `#FFFFFF` | 黒シャドウ |
| 夜 | `#FFFFFF` | 黒シャドウ |
| 曇り / 雨系 | `#FFFFFF` | 黒シャドウ |

---

## 画像パターン

### 時間帯 × 天気 = 40パターン

**時間帯（4種）**

| ID | 名前 | 時間範囲 |
|----|------|----------|
| `morning` | 朝 | 6:00 - 11:59 |
| `afternoon` | 昼 | 12:00 - 16:59 |
| `evening` | 夕方 | 17:00 - 19:59 |
| `night` | 夜 | 20:00 - 5:59 |

**天気（10種）**

| ID | 名前 | 絵文字 |
|----|------|--------|
| `clear` | 快晴 | ☀️ |
| `sunny` | 晴れ | 🌤️ |
| `cloudy` | 曇り | ☁️ |
| `fog` | 霧 | 🌫️ |
| `light_rain` | 小雨 | 🌧️ |
| `rain` | 雨 | 🌧️ |
| `thunderstorm` | 雷雨 | ⛈️ |
| `snow` | 雪 | 🌨️ |
| `blizzard` | 吹雪 | ❄️ |
| `sleet` | みぞれ | 🌨️ |

### 画像ファイル命名規則

```
{time_of_day}_{weather}.png

例:
morning_clear.png
afternoon_rain.png
evening_thunderstorm.png
night_snow.png
```

---

## 画像生成

### 使用モデル

| モデル | 用途 |
|--------|------|
| `gemini-2.5-flash-image` | 40パターンの事前生成 |

### 生成プロンプト（テンプレート）

```
Present a clear, 45° top-down isometric miniature 3D cartoon 
scene of a generic modern city, featuring stylized buildings 
and urban elements (NO specific landmarks). 

Use soft, refined textures with realistic PBR materials and 
gentle, lifelike lighting and shadows for [TIME_OF_DAY] time.

Integrate [WEATHER] conditions directly into the city 
environment to create an immersive atmospheric mood. 

Use a clean, minimalistic composition with a soft, 
solid-colored background.

Leave the top-center area clean for text overlay 
(city name, weather icon, date, temperature).
```

### 画像仕様

| 項目 | 値 |
|------|-----|
| アスペクト比 | 16:9 |
| 解像度 | 1K |
| 形式 | PNG |
| 再生成頻度 | 月1回 |

---

## 技術スタック

### Android アプリ

| コンポーネント | 技術 |
|----------------|------|
| 言語 | Kotlin |
| UI Framework | Jetpack Compose |
| Widget | Jetpack Glance |
| ネットワーク | Retrofit + OkHttp |
| 画像読み込み | Coil |
| 位置情報 | Google Play Services Location |
| バックグラウンド処理 | WorkManager |
| データ保存 | DataStore |
| Minimum SDK | API 26 (Android 8.0) |

### バックエンド

| コンポーネント | 技術 |
|----------------|------|
| API サーバー | Cloudflare Workers |
| 画像ストレージ | Cloudflare R2 |
| Weather API | OpenWeatherMap (Free tier) |
| 画像生成 | Gemini API |

---

## API 設計

### エンドポイント

#### GET `/api/weather`

現在地の天気情報と画像URLを取得

**リクエスト**

```
GET /api/weather?lat={latitude}&lon={longitude}
```

**レスポンス**

```json
{
  "city": "Tokyo",
  "temperature": 18,
  "weather": "sunny",
  "time_of_day": "afternoon",
  "date": "2024-12-04",
  "image_url": "https://r2.example.com/afternoon_sunny.png",
  "icon": "🌤️",
  "text_color": "#FFFFFF",
  "shadow_color": "#000000"
}
```

#### GET `/api/images/{time_of_day}_{weather}.png`

プリレンダリング済み画像を取得（R2から配信）

---

## ディレクトリ構成

### Android

```
app/src/main/
├── java/com/example/dioramaweather/
│   ├── MainActivity.kt
│   ├── DioramaWeatherApp.kt
│   ├── data/
│   │   ├── api/
│   │   │   ├── WeatherApi.kt
│   │   │   └── WeatherApiService.kt
│   │   ├── model/
│   │   │   ├── WeatherData.kt
│   │   │   └── WeatherResponse.kt
│   │   └── repository/
│   │       └── WeatherRepository.kt
│   ├── widget/
│   │   ├── WeatherWidget.kt
│   │   ├── WeatherWidgetReceiver.kt
│   │   └── WeatherWidgetWorker.kt
│   ├── location/
│   │   └── LocationService.kt
│   └── ui/
│       ├── theme/
│       │   └── Theme.kt
│       └── settings/
│           └── SettingsScreen.kt
├── res/
│   ├── xml/
│   │   └── weather_widget_info.xml
│   ├── drawable/
│   │   └── widget_preview.png
│   └── values/
│       └── strings.xml
└── AndroidManifest.xml
```

### バックエンド

```
backend/
├── src/
│   ├── index.ts
│   ├── weather.ts
│   └── utils.ts
├── wrangler.toml
└── package.json
```

---

## 更新頻度

| 項目 | 頻度 |
|------|------|
| 天気データ取得 | 30分ごと |
| ウィジェット表示更新 | 30分ごと |
| 画像再生成 | 月1回 |

---

## OpenWeatherMap 天気コード マッピング

```kotlin
fun mapWeatherCode(code: Int): String {
    return when (code) {
        in 200..232 -> "thunderstorm"
        in 300..321 -> "light_rain"
        in 500..504 -> "rain"
        511 -> "sleet"
        in 520..531 -> "rain"
        in 600..602 -> "snow"
        in 611..616 -> "sleet"
        in 620..622 -> "snow"
        in 615..616 -> "blizzard"
        in 701..741 -> "fog"
        in 751..781 -> "fog"
        800 -> "clear"
        801 -> "sunny"
        in 802..804 -> "cloudy"
        else -> "cloudy"
    }
}
```

---

## 権限

### Android

| 権限 | 用途 |
|------|------|
| `INTERNET` | API通信 |
| `ACCESS_FINE_LOCATION` | 現在地取得（精密） |
| `ACCESS_COARSE_LOCATION` | 現在地取得（粗い） |
| `ACCESS_BACKGROUND_LOCATION` | バックグラウンドでの位置更新 |

---

## 将来の拡張

- [ ] iOS 版 (WidgetKit)
- [ ] 複数都市対応
- [ ] ウィジェットサイズバリエーション (2x2, 4x4)
- [ ] リアルタイム画像生成オプション
- [ ] カスタムテーマ
- [ ] 天気予報（5日間）表示

---

## 参考リンク

- [Gemini API Image Generation](https://ai.google.dev/gemini-api/docs/image-generation)
- [Jetpack Glance](https://developer.android.com/jetpack/compose/glance)
- [OpenWeatherMap API](https://openweathermap.org/api)
- [Cloudflare Workers](https://developers.cloudflare.com/workers/)
- [Cloudflare R2](https://developers.cloudflare.com/r2/)
