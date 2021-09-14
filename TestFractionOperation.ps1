#
# PowerShell script to execute and verify test casea for FractionOperation.java.
# Test data is read from CSV file TestData.csv located in the current directory.
# TestData.csv contains one line per test case, in the following format:
#
# Operand1,Operator,Operand2,ExpectedResult
#
# For each test, the input expression, expected result, and actual result are
# written to stdout, as is the pass/fail status.
#
# The total number of tests, the number passed, and the number failed are reported.
#

$test=0
$pass=0
$fail=0
$operator=$null

$CSV = Import-Csv ".\TestData.csv"
foreach($LINE in $CSV)
{
	Write-Host " "
	$test++
	Write-Host "Test number $test"
	
	$InputExpression = $($LINE.Operand1) + " " + $($LINE.Operator) + " " + $($LINE.Operand2)
    Write-Host "Input: $InputExpression"
	Write-Host "ExpectedResult: $($LINE.ExpectedResult)"
	
	if ( $($LINE.Operator) -eq '*')
	{
		$operator = '"*"'
	} else {
		$operator = $($LINE.Operator)
	}
	
	$ActualResult = (java FractionOperation $($LINE.Operand1) $operator $($LINE.Operand2))
	
	Write-Host "Actual Result: $ActualResult"
	if ( $ActualResult -eq $($LINE.ExpectedResult) )
	{
		"Test passed"
		$pass++
	} else {
		"Test failed <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
		$fail++
	}
}
Write-Host " "
Write-Host "Tests Executed: $test"
Write-Host "Tests Passed: $pass"
Write-Host "Tests Failed: $fail"
Write-Host " "
