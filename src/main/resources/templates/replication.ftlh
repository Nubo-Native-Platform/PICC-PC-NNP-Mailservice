Hello ${name},
Your request for the installation of components on environment ${environmentId} is complete now as follows.
	
        <#list comp?split(",") as c>
            ${c}
        </#list>
   
Please get in touch with support at ${nnpEmail} in case any help is required.
Regards,
${nnpAdmin}

