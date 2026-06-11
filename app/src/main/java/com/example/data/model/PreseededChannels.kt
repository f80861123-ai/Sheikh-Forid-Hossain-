package com.example.data.model

object PreseededChannels {
    fun getList(): List<Channel> = listOf(
        // === BANGLADESH ===
        Channel(
            id = "bd_gtv",
            name = "GTV Live (গাজী টিভি)",
            logoUrl = "https://images.prothomalo.com/prothomalo-bangla/2021-06/eb6cff3b-d2fd-4f91-88f1-67f781dfa544/GTV.jpg",
            streamUrl = "http://203.76.103.11:8001/ch/gtv/index.m3u8", // Direct local ISP HLS or public stream
            country = "Bangladesh",
            category = "Sports",
            isFeatured = true,
            isTrending = true,
            isMostWatched = true,
            simulatedViewerCount = 12450
        ),
        Channel(
            id = "bd_t_sports",
            name = "T Sports Live (টি স্পোর্টস)",
            logoUrl = "https://www.sportsnews-bd.com/wp-content/uploads/2020/11/T-Sports-Logo.jpg",
            streamUrl = "http://103.197.207.24:8181/tsports/index.m3u8",
            country = "Bangladesh",
            category = "Sports",
            isFeatured = true,
            isTrending = true,
            isMostWatched = true,
            simulatedViewerCount = 18900
        ),
        Channel(
            id = "bd_btv_world",
            name = "BTV World (বিটিভি ওয়ার্ল্ড)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/en/2/23/Bangladesh_Television_logo.svg",
            streamUrl = "https://btvworld.live/live/stream.m3u8", // Fallback placeholder or public HLS
            country = "Bangladesh",
            category = "General",
            isFeatured = false,
            simulatedViewerCount = 3120
        ),
        Channel(
            id = "bd_somoy",
            name = "Somoy TV (সময় টিভি)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e0/Somoy_TV_Logo.png",
            streamUrl = "https://somoytv-live.cdngg.net/somoytv/somoytv.smil/playlist.m3u8",
            country = "Bangladesh",
            category = "News",
            isTrending = true,
            simulatedViewerCount = 14500
        ),
        Channel(
            id = "bd_jamuna",
            name = "Jamuna TV (যমুনা টিভি)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/e/eb/Jamuna_TV_Logo.png",
            streamUrl = "https://jamunasingle-somoytv.cdngg.net/jamunatv/jamunatv.smil/playlist.m3u8",
            country = "Bangladesh",
            category = "News",
            simulatedViewerCount = 9800
        ),

        // === INDIA ===
        Channel(
            id = "in_sports_star",
            name = "Star Sports (Hindi/English)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/3/36/Star_Sports_logo.svg",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8", // Sourced test stream
            country = "India",
            category = "Sports",
            isFeatured = true,
            isTrending = true,
            simulatedViewerCount = 23100
        ),
        Channel(
            id = "in_dd_sports",
            name = "DD Sports National",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/7/74/DD_Sports_logo%28vertical%29.png",
            streamUrl = "https://ddsports-live.akamaized.net/hls/live/2042790/ddsports/master.m3u8",
            country = "India",
            category = "Sports",
            isFeatured = true,
            simulatedViewerCount = 8900
        ),
        Channel(
            id = "in_aaj_tak",
            name = "Aaj Tak Live News",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/d/df/Aaj_Tak_logo.svg",
            streamUrl = "https://aajtak-live.akamaized.net/hls/live/2014312/aajtak/aajtak_1.m3u8",
            country = "India",
            category = "News",
            simulatedViewerCount = 15300
        ),

        // === USA ===
        Channel(
            id = "us_redbull",
            name = "Red Bull TV Live (Global)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e5/Red_Bull_TV_Logo.png",
            streamUrl = "https://rbmn-live.secure.footprint.net/v1/manifest/hls/dvr/at/2/master.m3u8",
            country = "USA",
            category = "Sports",
            isFeatured = true,
            isTrending = true,
            isMostWatched = true,
            simulatedViewerCount = 37200
        ),
        Channel(
            id = "us_cbs_news",
            name = "CBS News Live (US)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/7/72/CBS_News_logo.svg",
            streamUrl = "https://cbsn-us.cbsnstream.cbsnews.com/main/manifest.m3u8",
            country = "USA",
            category = "News",
            simulatedViewerCount = 11200
        ),
        Channel(
            id = "us_nasa",
            name = "NASA TV Public HD",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e5/NASA_logo.svg",
            streamUrl = "https://ntv1.akamaized.net/hls/live/2014027/NASA-NTV1-HLS/master.m3u8",
            country = "USA",
            category = "Documentary",
            simulatedViewerCount = 4500
        ),

        // === UNITED KINGDOM ===
        Channel(
            id = "uk_sky_news",
            name = "Sky News International",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/5/53/Sky_News_logo_2020.svg",
            streamUrl = "https://skynews-live.akamaized.net/hls/live/2014317/skynews/master.m3u8",
            country = "UK",
            category = "News",
            isTrending = true,
            simulatedViewerCount = 16900
        ),
        Channel(
            id = "uk_edge_sport",
            name = "Edge Sport Live",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/5/5f/Placeholder_no_image.svg",
            streamUrl = "https://edgesport.secure.footprint.net/v1/manifest/hls/dvr/at/3/master.m3u8",
            country = "UK",
            category = "Sports",
            isFeatured = true,
            simulatedViewerCount = 8300
        ),

        // === PAKISTAN ===
        Channel(
            id = "pk_ten_sports",
            name = "Ten Sports Pakistan",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/a/ab/Ten_Sports_Logo.png",
            streamUrl = "https://test-streams.mux.dev/x36xhg/x36xhg.m3u8", // Sourced test stream or public HLS
            country = "Pakistan",
            category = "Sports",
            isFeatured = true,
            simulatedViewerCount = 7400
        ),
        Channel(
            id = "pk_ary_news",
            name = "ARY News Live",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/f/ff/ARY_News_Logo.png",
            streamUrl = "https://arynews-live.akamaized.net/hls/live/2014285/arynews/master.m3u8",
            country = "Pakistan",
            category = "News",
            simulatedViewerCount = 12900
        ),

        // === SAUDI ARABIA ===
        Channel(
            id = "sa_sbc_sports",
            name = "KSA Sports HD (Saudi)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/a/aa/KSA_Sports_logo.svg",
            streamUrl = "https://rtmp.sbc.sa/live/sports1.m3u8",
            country = "Saudi Arabia",
            category = "Sports",
            isFeatured = true,
            isMostWatched = true,
            simulatedViewerCount = 21000
        ),
        Channel(
            id = "sa_quran",
            name = "Makkah Live (Quran TV)",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/7/77/Saudi_Quran_TV_logo.png",
            streamUrl = "https://saudi-quran.akamaized.net/hls/live/2042783/quran/master.m3u8",
            country = "Saudi Arabia",
            category = "Religious",
            isTrending = true,
            simulatedViewerCount = 31000
        ),

        // === UAE ===
        Channel(
            id = "ae_dubai_sports",
            name = "Dubai Sports HD",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/b/be/Dubai_Sports_logo.svg",
            streamUrl = "https://dmi-sports-live.akamaized.net/hls/live/2000346/sports10/master.m3u8",
            country = "UAE",
            category = "Sports",
            isFeatured = true,
            simulatedViewerCount = 9200
        ),

        // === CANADA ===
        Channel(
            id = "ca_cbc_news",
            name = "CBC News Canada",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/c/cb/CBC_News_logo.svg",
            streamUrl = "https://cbcnews-live.akamaized.net/hls/live/2014316/cbcnews/master.m3u8",
            country = "Canada",
            category = "News",
            simulatedViewerCount = 6800
        ),

        // === AUSTRALIA ===
        Channel(
            id = "au_abc_news",
            name = "ABC News Australia",
            logoUrl = "https://upload.wikimedia.org/wikipedia/commons/4/41/ABC_News_logo_2018.svg",
            streamUrl = "https://abcnews-live.akamaized.net/hls/live/2014315/abcnews/master.m3u8",
            country = "Australia",
            category = "News",
            simulatedViewerCount = 7100
        )
    )
}
