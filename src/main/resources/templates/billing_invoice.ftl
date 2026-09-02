<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>NNP Billing Invoice</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f9; color: #333; margin: 0; padding: 20px; }
        .invoice-card { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px; }
        .header { text-align: center; border-bottom: 2px solid #e2e8f0; padding-bottom: 15px; margin-bottom: 20px; }
        .header h2 { color: #2b6cb0; margin: 0; }
        .details-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        .details-table td { padding: 8px 12px; font-size: 14px; }
        .details-table td.label { font-weight: bold; color: #4a5568; width: 40%; }
        .item-table { width: 100%; border-collapse: collapse; margin-top: 15px; margin-bottom: 20px; }
        .item-table th, .item-table td { border: 1px solid #e2e8f0; padding: 10px; text-align: left; font-size: 14px; }
        .item-table th { background-color: #edf2f7; color: #2d3748; }
        .total-row { font-weight: bold; background-color: #ebf8ff; }
        .footer { text-align: center; font-size: 12px; color: #a0aec0; margin-top: 25px; border-top: 1px solid #e2e8f0; padding-top: 15px; }
    </style>
</head>
<body>
    <div class="invoice-card">
        <div class="header">
            <h2>NNP Platform Billing Statement</h2>
            <p style="margin-top: 5px; font-size: 13px; color: #718096;">Account Statement &amp; Usage Summary</p>
        </div>

        <table class="details-table">
            <tr>
                <td class="label">Account Name:</td>
                <td>${accountName}</td>
            </tr>
            <#if billId??>
            <tr>
                <td class="label">Invoice ID:</td>
                <td>${billId}</td>
            </tr>
            </#if>
            <tr>
                <td class="label">Billing Period:</td>
                <td>${periodStart} to ${periodEnd}</td>
            </tr>
        </table>

        <table class="item-table">
            <thead>
                <tr>
                    <th>Description</th>
                    <th style="text-align: right;">Amount (USD)</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>AI / Token Usage Charges</td>
                    <td style="text-align: right;">$${tokenCost}</td>
                </tr>
                <tr>
                    <td>Active K8s Component Hosting Charges</td>
                    <td style="text-align: right;">$${compCost}</td>
                </tr>
                <tr class="total-row">
                    <td>TOTAL AMOUNT DUE</td>
                    <td style="text-align: right; color: #2b6cb0;">$${totalAmount}</td>
                </tr>
            </tbody>
        </table>

        <p style="font-size: 13px; color: #4a5568;">
            Thank you for using the NNP Platform. If you have any questions regarding this invoice, please contact support.
        </p>

        <div class="footer">
            &copy; NNP Platform Billing System. All rights reserved.
        </div>
    </div>
</body>
</html>
