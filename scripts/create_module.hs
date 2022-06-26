import System.Directory (createDirectoryIfMissing)
import System.FilePath.Posix (takeDirectory)

main :: IO ()
main = do
  putStr "Enter module name: "
  name <- getLine
  createModule name
  addToSettingsGradle name
  putStrLn $ "Module " ++ name ++ " created."

createModule :: String -> IO ()
createModule name = do
  let moduleRoot = name ++ "/"
  createDirTree $ moduleRoot ++ "src/main/java/com/ivy/" ++ name ++ "/"
  writeFile (name ++ "/src/main/AndroidManifest.xml") (manifest name)

  buildGradle <- readBuildGradle
  writeFile (moduleRoot ++ "build.gradle.kts") buildGradle

  writeFile (moduleRoot ++ ".gitignore") gitIgnore

addToSettingsGradle :: String -> IO ()
addToSettingsGradle name = appendFile "settings.gradle.kts" (includeStm name)
  where
    includeStm :: String -> String
    includeStm name = "\ninclude(\":" ++ name ++ "\")"

gitIgnore :: String
gitIgnore = "/build"

manifest :: String -> String
manifest name = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest package=\"com.ivy." ++ name ++ "\"/>"

readBuildGradle :: IO String
readBuildGradle = readFile "scripts/templates/build.gradle.kts.template"

createDirTree :: FilePath -> IO ()
createDirTree path = do
  createDirectoryIfMissing True $ takeDirectory path
