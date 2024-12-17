# Modpack Downloader

This is a mod to auto update minecraft modpacks on startup.

## How it works
This mod downloads a json file from a configurable http(s) server and compares its version number to the one locally stored in `pack.json`.


## Files
All filenames **exept `pack.json`** are reccommendations and can be changed.

### `pack.json`
This file is located on the **client**.
This file **has to** be located in the **root** directory of the modpack.

```json
{
  "metaUrl": string // url of meta.json
  "version": int // version currently installed (-1 => download fresh.zip)
}
```

### `meta.json`
This file should be publicly hosted on a http(s) **server**.

```json
{
  "version": int // The newest version available
  "freshUrl": string // Url of fresh.zip (version 0)
  "updateUrls": string[] // list of urls to the update zips (first url = update from version 0 to version 1)
}
```

### `fresh.zip`
This file should be publicly hosted on a http(s) **server**.
This file contains the base install of the modpack.

#### File Structure
same as update.zip

### `update.zip`
This file should be publicly hosted on a http(s) **server**.
Every update should have a unique url.
Past updates should stay available, so users who haven't played in a while can update to the newest version.

#### File Structure
A `pack.json` file located in the .zip will be ignored and overridden.

```
|
|- delete.json
|- download.json
|- ...files and directories that should be overridden / added
```

#### `delete.json`
This file gets deleted after updating.
This file lists files, that should be deleted.
All paths are relative to the root directory of the modpack.

```json
[
  "test.file",
  "mods/test-mod.jar",
  "config/test-config.json",
  ...
]
```

#### `download.json`
This file gets deleted after updating.
This file lists files to download from the internet.
**URLs have to be direct download links!**
Directories will not be created automatically, they have to be present in the .zip file.

```json
{
  "path to save file to": "URL...",
  "mods/test-mod.jar": "https://example.com/some/url/test-mod-v1-0-0.jar",
  ...
}
```
