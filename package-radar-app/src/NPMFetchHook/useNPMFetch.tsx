import { useState, useEffect } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
import { NpmFetchSuccessResponseSchema, type NpmFetchSuccessResponse } from "./NpmFetchSuccessResponse";
import API_BASE_URL from "../config/api";

function useNPMFetch(queriedSearch: SearchContextStructure|null, signal: AbortSignal|null|undefined){
    //const [queryResult, setQueryResult] = useState(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [api_response, setApi_Response] = useState<NpmFetchSuccessResponse | null>(null);
    useEffect(() => {

        if(!queriedSearch || (signal == null || signal == undefined)){
            return;
        }

        const fetchFromBackend = async () => {
            setLoading(true);
            let api_root = `${API_BASE_URL}/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}`;
            let api_branch = `/latest`;
            try{
                let response = null;
                if(queriedSearch.search_term.length == 2){
                    api_root = `${API_BASE_URL}/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}/${queriedSearch.search_term[1]}`;
                }
                if(queriedSearch.reportId !== undefined &&  queriedSearch.reportId !== null){
                    api_branch = `/id/${queriedSearch.reportId}`;
                }


                response = await fetch(api_root + api_branch, {signal,}); //Listens to signal object
                if(signal?.aborted){
                    return;
                }
                if(response?.status == 200){
                    try{
                        const info = await response.json();
                        const packageData = NpmFetchSuccessResponseSchema.parse(info);                        
                        setApi_Response(packageData);
                        setFetchError(null);
                    }
                    catch(error){
                        console.log("Zod Error:", error)
                    }
                }
                else{
                    setFetchError("Response was empty");
                }
            }
            catch(reponseError: unknown){
                if(reponseError instanceof Error){
                    setFetchError("fetchFromBackend: " + reponseError);
                }
                else{
                    setFetchError("Unkown Error");
                }
            }
            setLoading(false);
        }

        fetchFromBackend();
    }, [queriedSearch, signal])

    return {api_response, loading, fetchError}
}
export default useNPMFetch;