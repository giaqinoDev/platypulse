import { useEffect, useState } from "react";

interface Dependents{
    num_dependents: number|null,
    num_dependent_repos: number|null
}

function useLibrariesIOFetch(query: string|null, platform: string, setFetchError: React.Dispatch<React.SetStateAction<string|null|Error>>){//{query, platform, setFetchError}:UseLibrariesIOFetchProps ){
    //MAKE SURE TO HIDE API KEY!!!!!!!!!!!!!!!!
    const BASE_LIBRARIES_URL = `https://libraries.io/api/${platform}/${query}?api_key=***REMOVED***`;
    const [packageDependents, setPackageDependents] = useState<Dependents|null>(null);
    useEffect(() => {

        const fecthLibrariesIOPackage = async () => {
            if(!query){
                return;
            }
            try{
                const response = await fetch(BASE_LIBRARIES_URL);
                if(response.status >= 500){
                    throw new Error("fecthLibrariesIOPackage: Server error");
                }
                else if(response.status >= 400){
                    throw new Error("fecthLibrariesIOPackage: Client side error");
                }

                const packageInfo = await response.json();
                const dependents: Dependents = {
                    num_dependents: packageInfo.dependents_count,
                    num_dependent_repos: packageInfo.dependent_repos_count
                }
                setPackageDependents(dependents);
            }
            catch(responseError: unknown){
                if(responseError instanceof Error){
                    setFetchError("fecthLibrariesIOPackage:" + responseError.message);
                }
                else{
                    setFetchError("Unkown error")
                }
            }
        }

        fecthLibrariesIOPackage();
    }, [query, platform])

    return packageDependents;//{num_dependents, num_dependent_repos};
}

export default useLibrariesIOFetch;