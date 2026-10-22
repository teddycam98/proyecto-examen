$ErrorActionPreference = "Stop"

$htmlPath = "c:\Proyectos\proyecto-examen\docs\Guia_Exposicion_Libreria_Vico.html"
$desktopDocx = "C:\Users\HellBoy\Desktop\Guia_Exposicion_Libreria_Vico.docx"
$docsDocx = "c:\Proyectos\proyecto-examen\docs\Guia_Exposicion_Libreria_Vico.docx"

Write-Host "Iniciando conversion con Microsoft Word COM..."

$word = $null
$doc = $null

try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $word.DisplayAlerts = [Microsoft.Office.Interop.Word.WdAlertLevel]::wdAlertsNone

    Write-Host "Abriendo archivo HTML: $htmlPath"
    $doc = $word.Documents.Open($htmlPath)

    # wdFormatXMLDocument = 16 (.docx)
    Write-Host "Guardando en Escritorio: $desktopDocx"
    $doc.SaveAs2($desktopDocx, 16)

    Write-Host "Guardando copia en docs/: $docsDocx"
    $doc.SaveAs2($docsDocx, 16)

    $doc.Close()
    $doc = $null

    Write-Host "Conversion finalizada con exito."
} catch {
    Write-Host "Error durante la conversion: $_"
    throw $_
} finally {
    if ($doc -ne $null) {
        $doc.Close([Microsoft.Office.Interop.Word.WdSaveOptions]::wdDoNotSaveChanges)
    }
    if ($word -ne $null) {
        $word.Quit()
        [System.Runtime.InteropServices.Marshal]::ReleaseComObject($word) | Out-Null
    }
    [System.GC]::Collect()
    [System.GC]::WaitForPendingFinalizers()
}
