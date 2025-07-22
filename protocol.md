## типы
string = [u32 - length in bytes] [utf-8 encoded characters]

videoinfo = [u32 - segment amount] [u8 - segment length] [u8 - max quality] [string - author name] [string - title] [string - description]

## пакеты
пакет от клиента к серверу начинается с байта команды

### 0x00 - получить инфу о видео
**С->S**: 0x00 [u32 - video id]

**S->C**: [videoinfo]

### 0x01 - получить сегмент видео
**С->S**: 0x01 [u32 - video id] [u32 - segment id] [u8 - quality]

**S->C**: [u32 - size] [bytes]

### 0x02 - список видео
**С->S**: 0x02

**S->C**: [u32 - count] count*([u32 - video id] [videoinfo])

### 0x03 - войти в аккаунт
**С->S**: 0x03 [string - username] [string - password]

если успешно: **S->C**: 0x00 [string - token]

если нет такого аккаунта: **S->C**: 0x01

если неверный пароль: **S->C**: 0x02

### 0x04 - создать аккаунт
**С->S**: 0x04 [string - username] [string - password]

если успешно: **S->C**: 0x00 [string - token]

если ник занаят: **S->C**: 0x01

если ник/пароль не подходят: **S->C**: 0x02

(например если в нике спец. символы или пароль пустой)

### 0x05 - загрузить видео
**С->S**: 0x05 [string - token] [string - title] [string - description] [u64 - file size] [file bytes]

пока обрабатывается: **S->C**: 0x00 [u8 - progress]

отправляется много раз, progress в процентах (от 0 до 100)

если ошибка: **S->C**: 0x01

если успешно: **S->C**: 0x02 [u32 - video id]

### 0x06 - редактировать видео
**C->S**: 0x05 [string - token] [u32 - video id] [u8 - edit description] [edit data: [string - title] (if needed) [string - description] (if needed) [u64 - file size] [file bytes] (if needed)]

Обозначение изменений в edit description:

Бит 1 (**0x80**) - редактирование названия

Бит 2 (**0x40**) - редактирование описания

Бит 3 (**0x20**) - редактирование (повторная загрузка) данных

5 младших битов зарезервированы

Возможно редактирование нескольких полей одновременно, для этого сложите необходимые сигналы.

Например: отредактировать название и описание = бит 1 + бит 2 = _0x08 + 0x04 = 0xC0_

Данные передаются в порядке: **название -> описание -> видео**, с пропуском при необходимости

Название и описание передаются как строки, файл как [u64 - file size] [file bytes]