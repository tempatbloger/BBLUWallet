# BBLU Wallet - Android Cryptocurrency Wallet for Bitcoin-Blu (BBLU)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-7.0+-green.svg)](https://developer.android.com)

**BBLU Wallet** adalah aplikasi Android wallet untuk cryptocurrency **Bitcoin-Blu (BBLU)** - sebuah fork dari Bitcoin.

## Parameter BBLU vs Bitcoin

| Parameter | Bitcoin | BBLU |
|-----------|---------|------|
| Address Prefix | bc1 | bb1 |
| Derivation Path | m/84/0/0/0 | m/84/4353123/0/0 |
| PubKeyHash | 0 | 25 |
| ScriptHash | 5 | 26 |
| Bech32 HRP | bc | bb |

## Fitur

- Create & Restore Wallet
- Send BBLU
- Receive BBLU with QR Code
- Copy Address

## Cara Install

Download APK dari Releases atau build sendiri:

`./gradlew assembleDebug`

## Struktur Project

- app/src/main/java/com/bblu/wallet/ui/ - Activities
- app/src/main/java/com/bblu/wallet/wallet/ - Wallet Manager
- app/src/main/java/com/bblu/wallet/network/ - Network Parameters

## License

MIT License

---
Dibuat untuk komunitas Bitcoin-Blu
