# BBLU Wallet - Android Cryptocurrency Wallet for Bitcoin-Blu (BBLU)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-7.0+-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)

**BBLU Wallet** adalah aplikasi Android wallet untuk cryptocurrency **Bitcoin-Blu (BBLU)** - sebuah fork dari Bitcoin dengan parameter unik dan custom derivation path.

## 📱 Fitur Utama

- ✅ Generate & Restore Wallet dengan 12/24 kata seed phrase
- ✅ Kirim dan Terima BBLU
- ✅ QR Code untuk address
- ✅ Lihat riwayat transaksi
- ✅ Koneksi ke Electrum Server BBLU
- ✅ Dukungan Bech32 address (prefix `bb`)
- ✅ Balance real-time

## 🔧 Parameter BBLU vs Bitcoin

| Parameter | Bitcoin | BBLU |
|-----------|---------|------|
| Address Prefix | `bc1` | `bb1` |
| Derivation Path | `m/84'/0'/0'/0` | `m/84'/4353123'/0'/0` |
| PubKeyHash | `0` | `25` |
| ScriptHash | `5` | `26` |
| Bech32 HRP | `bc` | `bb` |
| Electrum Server | `electrumx.bitcoin.org` | `electrumx.bitcoin-blu.org:50001` |

## 🚀 Quick Start

### Prasyarat

- Android Studio Hedgehog | 2023.3.1 atau lebih baru
- JDK 17
- Android SDK API 24+

### Clone Repository

```bash
git clone https://github.com/tempatbloger/BBLUWallet.git
cd BBLUWallet
