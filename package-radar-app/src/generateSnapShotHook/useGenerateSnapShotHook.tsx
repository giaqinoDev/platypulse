import { useState } from "react";
import { GenReportResponseSchema } from "./GenReportResponse";

function useGenerateSnapShot(signal: AbortSignal|null|undefined){
    const [generatingReport, setGeneratingReport] = useState<boolean>(false);
    const [generationError, setGenerationError] = useState<string|null|Error>(null);
    const [generatedReportId, setGeneratedReportId] = useState<null|string>();

    async function generateNewReport(packageId: string|null|undefined){
        if(!packageId || signal == null || signal == undefined){
            return;
        }

        setGeneratingReport(true);
        try{
            let response = null;
            response = await fetch(`/bend/api/package/npm/${packageId}/generateReport`, {method: "POST", signal,});
            if(signal?.aborted){
                return;
            }

            if(response.status == 200){
                const data = await response.json();
                const generated_report_id = GenReportResponseSchema.parse(data);
                setGeneratedReportId(generated_report_id);
            }
            else{
                setGenerationError("Issue Generating Report");
            }
        }
        catch(responseError: unknown){
            console.log("Snapshot request threw an error:", responseError);
            
            if(responseError instanceof Error){
                setGenerationError("Backend Error: " + responseError);
            }
            else{
                setGenerationError("Unkown Error");
            }
        }
        setGeneratingReport(false);
    }

    return {generateNewReport, generatingReport, generationError, generatedReportId}
}

export default useGenerateSnapShot;