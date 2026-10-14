using System;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;

namespace TypeRush
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            string appDir = AppDomain.CurrentDomain.BaseDirectory;
            string jarPath = Path.Combine(appDir, "TypeRush.jar");

            if (!File.Exists(jarPath))
            {
                MessageBox.Show(
                    "TypeRush.jar was not found in the application directory:\n" + appDir,
                    "TypeRush - Missing Core File",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Error);
                return;
            }

            string javaExe = FindJavaExecutable();
            if (string.IsNullOrEmpty(javaExe))
            {
                DialogResult res = MessageBox.Show(
                    "Java Runtime (JRE / JDK 17 or higher) is required to run TypeRush.\n\n" +
                    "Would you like to open the official Java download page now?",
                    "TypeRush - Java Required",
                    MessageBoxButtons.YesNo,
                    MessageBoxIcon.Information);
                if (res == DialogResult.Yes)
                {
                    try
                    {
                        Process.Start("https://adoptium.net/temurin/releases/");
                    }
                    catch { }
                }
                return;
            }

            try
            {
                ProcessStartInfo psi = new ProcessStartInfo();
                psi.FileName = javaExe;
                psi.Arguments = "-jar \"" + jarPath + "\"";
                psi.WorkingDirectory = appDir;
                psi.UseShellExecute = false;
                psi.CreateNoWindow = true;
                Process.Start(psi);
            }
            catch (Exception ex)
            {
                MessageBox.Show(
                    "Error launching TypeRush: " + ex.Message,
                    "TypeRush Launch Error",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Error);
            }
        }

        static string FindJavaExecutable()
        {
            // 1. Check JAVA_HOME
            string javaHome = Environment.GetEnvironmentVariable("JAVA_HOME");
            if (!string.IsNullOrEmpty(javaHome))
            {
                string jw = Path.Combine(javaHome, "bin", "javaw.exe");
                if (File.Exists(jw)) return jw;
                string j = Path.Combine(javaHome, "bin", "java.exe");
                if (File.Exists(j)) return j;
            }

            // 2. Check current directory jre/bin
            string localJre = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "jre", "bin", "javaw.exe");
            if (File.Exists(localJre)) return localJre;

            // 3. Search in system PATH
            string pathEnv = Environment.GetEnvironmentVariable("PATH");
            if (!string.IsNullOrEmpty(pathEnv))
            {
                string[] paths = pathEnv.Split(';');
                foreach (string p in paths)
                {
                    string trimmed = p.Trim();
                    if (string.IsNullOrEmpty(trimmed)) continue;
                    try
                    {
                        string jw = Path.Combine(trimmed, "javaw.exe");
                        if (File.Exists(jw)) return jw;
                    }
                    catch { }
                }
                foreach (string p in paths)
                {
                    string trimmed = p.Trim();
                    if (string.IsNullOrEmpty(trimmed)) continue;
                    try
                    {
                        string j = Path.Combine(trimmed, "java.exe");
                        if (File.Exists(j)) return j;
                    }
                    catch { }
                }
            }

            // 4. Common standard install locations
            string[] searchRoots = new string[] {
                Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles),
                Environment.GetFolderPath(Environment.SpecialFolder.ProgramFilesX86)
            };

            foreach (string root in searchRoots)
            {
                if (string.IsNullOrEmpty(root)) continue;
                string javaDir = Path.Combine(root, "Java");
                if (Directory.Exists(javaDir))
                {
                    try
                    {
                        string[] subDirs = Directory.GetDirectories(javaDir);
                        foreach (string s in subDirs)
                        {
                            string jw = Path.Combine(s, "bin", "javaw.exe");
                            if (File.Exists(jw)) return jw;
                        }
                    }
                    catch { }
                }

                string eclipseDir = Path.Combine(root, "Eclipse Adoptium");
                if (Directory.Exists(eclipseDir))
                {
                    try
                    {
                        string[] subDirs = Directory.GetDirectories(eclipseDir);
                        foreach (string s in subDirs)
                        {
                            string jw = Path.Combine(s, "bin", "javaw.exe");
                            if (File.Exists(jw)) return jw;
                        }
                    }
                    catch { }
                }
            }

            return "javaw.exe";
        }
    }
}
