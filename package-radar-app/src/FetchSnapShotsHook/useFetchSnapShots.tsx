import { useState } from "react";
import { SnapShotsTimelineSchema, type SnapShotTimeline } from "./SnapShot";
import API_BASE_URL from "../config/api";
function useFetchSnapShots(signal: AbortSignal|null|undefined){
    const [snapShotsLoading, setSnapShotsLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [fetchedSnapShots, setFetchedSnapShots] = useState<SnapShotTimeline|null>(null);

    async function getSnapShots(packageId: string|null|undefined){

        if(!packageId || signal == null || signal == undefined){
            return;
        }
        setSnapShotsLoading(true);

        try{
            let response = null;
            response = await fetch(`${API_BASE_URL}/api/package/${packageId}/snapshots`, {signal,});
            if(signal?.aborted){
                return;
            }
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
    }

    return {getSnapShots, snapShotsLoading, fetchError, fetchedSnapShots}
}

export default useFetchSnapShots;