#------------ Add path to environment variable -------------------------------------

$path2add = $PSScriptRoot + "\bin"
write-host $path2add

$systemAmiHome = [Environment]::GetEnvironmentVariable('AMI_HOME', 'machine');
$userAmiHome = [Environment]::GetEnvironmentVariable('AMI_HOME', 'user');
write-host $systemAmiHome
write-host $userAmiHome

$systemPath = [Environment]::GetEnvironmentVariable('Path', 'machine');
$userPath = [Environment]::GetEnvironmentVariable('Path', 'user');
write-host $systemPath
write-host $userPath

If ($systemPath.contains("%AMI_HOME%\bin")) {
    write-host "AMI_HOME is part of system PATH..."
    if ($systemAmiHome) {
        write-host "Updating system env var AMI_HOME from $systemAmiHome to $path2add..."
    } else {
        write-host "Setting system env var AMI_HOME from $systemAmiHome to $path2add..."
    }
    write-host "(If this fails, run this script as Administrator)"
    [Environment]::SetEnvironmentVariable('AMI_HOME', $path2add, 'machine');
    exit 0
}
If ($userPath.contains("%AMI_HOME%\bin")) {
    write-host "AMI_HOME is part of user PATH..."
    if ($userAmiHome) {
        write-host "Updating user env var AMI_HOME from $userAmiHome to $path2add..."
    } else {
        write-host "Setting user env var AMI_HOME from $userAmiHome to $path2add..."
    }
    [Environment]::SetEnvironmentVariable('AMI_HOME', $path2add, 'user');
    exit 0
}

If (!$userPath.contains($path2add)) {
    $userPath += $path2add
    $userPath = $userPath -join ';'
    [Environment]::SetEnvironmentVariable('Path', $userPath, 'user');
    write-host "Added to path!"
    write-host $userPath
} else {
    write-host "Path already contains $path2add"
}
