import { useState, type Dispatch, type SetStateAction } from "react";
import { SnapShotsTimelineSchema, type SnapShotTimeline } from "./SnapShot";

interface UseFetchSnapShotsInterface{
    setSnapShotsLoading: Dispatch<SetStateAction<boolean>>;
    setFetchError: Dispatch<SetStateAction<string|null|Error>>;
}
function useFetchSnapShots({setSnapShotsLoading, setFetchError}: UseFetchSnapShotsInterface){
    //const [snapShotsLoading, setSnapShotsLoading] = useState<boolean>(false);
    //const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [fetchedSnapShots, setFetchedSnapShots] = useState<SnapShotTimeline|null>();

    async function getSnapShots(packageId: string|null|undefined){

        if(!packageId){
            return;
        }
        setSnapShotsLoading(true);

        try{
            let response = null;
            response = await fetch(`/bend/api/package/${packageId}/snapshots`);
            if(response.status == 200){
                const data = await response.json();
                const snapshots = SnapShotsTimelineSchema.parse(data);
                setFetchedSnapShots(snapshots);
            }
            else{
                setFetchError("Response was empty");
            }
        }
        catch(responseError: unknown){
            if(responseError instanceof Error){
                setFetchError("Backend Error: " + responseError);
            }
            else{
                setFetchError("Unkown Error");
            }
        }
        setSnapShotsLoading(false);
        return fetchedSnapShots;
    }

    return {getSnapShots}
}

export default useFetchSnapShots;